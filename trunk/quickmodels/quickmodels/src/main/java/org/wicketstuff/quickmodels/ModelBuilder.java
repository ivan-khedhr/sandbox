/* Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.wicketstuff.quickmodels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.wicketstuff.persistence.*;
import org.wicketstuff.persistence.NewObjectFactory;
import org.wicketstuff.persistence.queries.QueryElement;
import static org.wicketstuff.quickmodels.QueryFields.*;
/**
 * Factory for wicket models based on a database query.  Typical usage is
 * to have some data for fetching objects from a database - for example,
 * a UUID passed in a URL.  e.g, 
 * <pre>
 * ModelBuilder<MyType> builder = Queries.UUID.builder(MyType.class);
 * builder.setUuid (theUuid);
 * PojoModel <MyType> wicketModel = builder.single();
 * </pre>
 * 
 * This class contains methods for setting all parameters of all supported
 * query types;  different types of queries have different sets of required,
 * optional and unused (illegal) parameters.  If a setter is called for a
 * parameter that is not actually used by the query type, an exception will
 * be thrown.
 * <p/>
 * A ModelBuilder is meant to be used once and then discarded.
 * <br/>
 * PENDING:  Remove field queries now that we have complex queries
 * Remove setType() method as this is always called in the constructor
 * 
 * @see Queries.builder()
 * @author Tim Boudreau
 */
public final class ModelBuilder<T, P> {
    private final Queries kind;
    private String uuid;
    private Collection<Long> uids;
    private Collection<String> uuids;
    private Comparator<T> comparator;
    private LoadFailurePolicy policy;
    private NewObjectFactory<T> factory;
    private Object object;
    private Class<P> fieldType;
    private P fieldValue;
    private String fieldName;
    private int newObjectCount = 1;
    private DbJob<?, Collection<T>, P> customQuery;
    private P customQueryArg;
    private long uid = -1L;
    private Class<T> type;
    private Collection<T> objects;
    

    ModelBuilder(Class<T> type, Queries kind) {
        this.type = type;
        this.kind = kind;
        if (!kind.getIllegalFields().contains(TYPE)) {
            usedField(TYPE, type);
        }
    }
    
    @Override
    public String toString() {
        return super.toString() + " for " + kind + ": uid=" + uid + "uuid="
                + uuid + " uids=" + uids + " uuids=" + uuids + " comparator " +
                comparator + " policy " + policy + " factory " + factory + 
                " object " + object + " fieldType " + fieldType + 
                " fieldName " + fieldName + " newObjectCount " + newObjectCount +
                " customQuery " + customQuery + " customQueryArg " + 
                customQueryArg + " type " + type + " objects " + objects;
    }
    
    /**
     * Create a model for a collection of persisted objects matching this
     * query.
     * @param db The database in question
     * @return A wicket model of a collection of objects matching this query
     * @throws IllegalStateException if not all required parameters needed to
     * complete this query have been set.  Note that setting a parameter to null
     * is the equivalent of unsetting it.
     */
    public <ContainerType> PojoCollectionModel<T> multi(Db<ContainerType> db) {
        checkRequiredFields();
        if (customQuery == null && kind() == Queries.CUSTOM_QUERY) {
            throw new IllegalStateException ("Custom query not set");
        }
        PojoCollectionModel.Template<T> t;
        switch (kind) {
        case COMPLEX :
            t = new PojoCollectionModel.ComplexQueryTemplate(type, (QueryElement) object,
                    comparator, policy, factory);
            break;
        case CUSTOM_QUERY :
            t = new PojoCollectionModel.CustomQueryTemplate(customQuery,
                    customQueryArg, policy, factory);
            break;
        case EXISTING_OBJECTS :
            //create a new list because the one we have may be unmodifiable
            //and if the comparator is used it will need to be sorted
            Collection<T> o;
            if (object == null) {
                o = new ArrayList<T>(objects);
            } else {
                o = new ArrayList<T>(Collections.<T>singletonList((T)object));
            }
            t = new PojoCollectionModel.ExistingObjectsTemplate<T>(type, o,
                    policy, factory);
            break;
        case NEW_OBJECT :
            t = new PojoCollectionModel.NewObjectsTemplate <T> (type,
                    newObjectCount, policy, factory);
            break;
        case OF_TYPE :
            t = new PojoCollectionModel.AllOfTypeTemplate<T> (type,
                    policy, factory, comparator);
            break;
        case PROTOTYPE :
            t = new PojoCollectionModel.PrototypeTemplate<T>((T)object,
                    policy, factory, comparator);
            break;
        case QUERY :
            t = new PojoCollectionModel.QueryTemplate<T, P>(type,
                    fieldName, fieldType,
                    fieldValue, policy, factory, comparator);
            break;
        case UID :
            Collection<Long> c = uids == null ? Collections.singleton(uid) :
                uids;
            t = new PojoCollectionModel.UidTemplate<T>(c, type, policy, factory);
            break;
        case UUID :
            Collection<String> cc = uuids == null ? Collections.singleton(uuid) :
                uuids;
            t = new PojoCollectionModel.UUidTemplate<T>(cc, type, policy, factory);
            break;
        default :
            throw new AssertionError("Kind not handled: " + kind);
        }
        return t.create(db);
    }
    
    /**
     * Create a wicket mdoel for a single object which matches this query
     * @param db The database to look the object up in
     * @return A wicket model for an object matching this query
     */
    public <ContainerType> PojoModel<T> single(Db <ContainerType> db) {
        if (comparator != null) {
            throw new IllegalArgumentException("Comparator was set but is " +
                    "unused unless you are creating a collection model");
        }
        if (objects != null) {
            throw new IllegalArgumentException("Object collection was set but is " +
                    "unused unless you are creating a collection model");
        }
        checkRequiredFields();
        PersistenceFacade<T> facade;
        switch (kind) {
        case COMPLEX :
            Collection<PersistenceFacade<T>> cc = 
                    db.getFacadeFactory().forComplexQuery (type, (QueryElement) object,
                    comparator, policy, factory);
            if (!cc.isEmpty()) {
                facade = cc.iterator().next();
            } else {
                facade = null;
            }
        case CUSTOM_QUERY :
            Collection <PersistenceFacade<T>> c;
            DbJob q = customQuery;
            if (q == null) {
                throw new IllegalStateException ("Custom query is null");
            }
            c = db.getFacadeFactory().<T,P>runCustomQuery(q, customQueryArg);
            if (!c.isEmpty()) {
                facade = c.iterator().next();
            } else {
                facade = null;
            }
            break;
        case EXISTING_OBJECTS :
            Object o = null;
            if (object != null) {
                o = object;
            } else if (objects != null && !objects.isEmpty()) {
                o = objects.iterator().next();
            }
            if (o == null) {
                throw new IllegalStateException("No objects to operate on for " + this);
            }
            facade = db.getFacadeFactory().forExisting((T)o);
            break;
        case NEW_OBJECT :
            facade = db.getFacadeFactory().forNew(type, factory);
            break;
        case OF_TYPE :
            facade = db.getFacadeFactory().forSingleton(type, policy, factory);
            break;
        case PROTOTYPE :
            Object oo = null;
            if (object != null) {
                oo = object;
            } else if (objects != null && !objects.isEmpty()) {
                oo = objects.iterator().next();
            }
            if (oo == null) {
                throw new IllegalStateException("No objects to operate on for " + this);
            }
            facade = db.getFacadeFactory().forPrototype((T)oo);
            break;
        case QUERY :
            facade = db.getFacadeFactory().forQuery(type, fieldName, fieldType, fieldValue, policy, factory);
            break;
        case UID :
            if (uid == -1L && uids == null || (uids != null &&uids.isEmpty())) {
                throw new IllegalStateException ("No UID for " + this);
            }
            long u2 = this.uid == -1L ? uids.iterator().next() : this.uid;
            facade = db.getFacadeFactory().forUid(type, u2, policy, factory);
            break;
        case UUID :
            if (uuid == null && uuids == null || (uuids != null && uuids.isEmpty())) {
                throw new IllegalStateException("No uuid for " + this);
            }
            String uu = uuid == null ? uuids.isEmpty() ? null : uuids.iterator().next() : uuid;
            if (uu == null) {
                throw new IllegalStateException("No uuid for " + this);
            }
            facade = db.getFacadeFactory().forUuid(uu, type, policy, factory);
            break;
        default :
            throw new AssertionError("Kind not handled: " + kind);
        }
        return new PojoModel<T>(facade);
    }
    
    private void checkRequiredFields() {
        Set <QueryFields> s = new HashSet <QueryFields> (kind.getRequiredFields());
        s.removeAll(usedFields);
        if (!s.isEmpty()) {
            throw new IllegalStateException ("The following required fields have " +
                    "not been set or were set to null: " + s);
        }
//        if (kind == Queries.PROTOTYPE && (object == null || objects == null || (objects != null && objects.isEmpty()))) {
//            throw new IllegalStateException(); //XXX find out what's wrong
//        }
    }
    
    private void checkSet(QueryFields f) {
        if (kind.getIllegalFields().contains(f)) {
            throw new IllegalArgumentException ("Field " + 
                    f + " is not allowed in queries of " + kind);
        }
    }
    
    private final Set<QueryFields> usedFields = new HashSet<QueryFields>();
    private void usedField (QueryFields f, Object o) {
        if (o != null) {
            checkSet(f);
            usedFields.add(f);
        } else {
            usedFields.remove(f);
        }
    }

    /**
     * Provide a custom query that will be run directly against the database
     * using its native interface.  Used with Queries.CUSTOM.
     * 
     * @param customQuery A custom query that returns a collection of the
     * appropriate type for this ModelBuilder.
     */
    public void setCustomQuery(DbJob<?, Collection<T>, P> customQuery) {
        usedField(CUSTOM_QUERY, customQuery);
        this.customQuery = customQuery;
    }

    /**
     * Set an optional argument to be passed into the run() method of the
     * DbJob passed to setCustomQuery().  Used with Queries.CUSTOM.
     * 
     * @param customQueryArg
     */
    public void setCustomQueryArg(P customQueryArg) {
        usedField(CUSTOM_QUERY_ARG, customQueryArg);
        this.customQueryArg = customQueryArg;
    }

    /**
     * Provide a NewObjectFactory which will be used to create a new instance
     * of this query's type in the event that no object in the database
     * matches the query.  Calling this method with a non-null value has
     * the side effect of setting the LoadFailurePolicy to 
     * LoadFailurePolicy.CREATE_NEW_OBJECT_ON_FAILURE.  Used with any query
     * type.
     * <p/>
     * If the type of object this query is for has a default constructor,
     * it is not necessary to provide a factory - just set the
     * policy to LoadFailurePolicy.CREATE_NEW_OBJECT_ON_FAILURE and
     * a new instance will be created by reflection.
     * 
     * @param factory A factory that can create objects.
     */
    public void setFactory(NewObjectFactory factory) {
        usedField(FACTORY, factory);
        this.factory = factory;
        usedField(POLICY, LoadFailurePolicy.CREATE_NEW_OBJECT_ON_FAILURE);
        this.policy = LoadFailurePolicy.CREATE_NEW_OBJECT_ON_FAILURE;
    }

    /**
     * Set the name of a field that should be matched.
     * 
     * Used with Queries.QUERY.
     * @param fieldName The name of the field
     */
    public void setFieldName(String fieldName) {
        usedField (FIELD_NAME, fieldName);
        this.fieldName = fieldName;
    }

    /**
     * Set the expected type of a field that should be matched.
     * 
     * Used with Queries.QUERY.
     * @param fieldName The type of the field
     */
    public void setFieldType(Class<P> fieldType) {
        usedField (FIELD_TYPE, fieldType);
        this.fieldType = fieldType;
    }

    /**
     * Set the expected value of a field that should be matched.
     * 
     * Used with Queries.QUERY.
     * @param fieldName The type of the field
     */
    public void setFieldValue(P fieldValue) {
        usedField (FIELD_VALUE, fieldValue);
        this.fieldValue = fieldValue;
    }

    /**
     * Set the number of new objects that should be created in the event
     * that the query cannot be satisfied.  Only meaningful in the case that
     * the LoadFailurePolicy is CREATE_NEW_OBJECT_ON_FAILURE and you are
     * calling QueryBuilder.multi() to get a model over multiple objects,
     * and want multiple new objects created in the case that the query fails.
     * 
     * @param newObjectCount The number of new objects to create in the case
     * of query failure.
     */
    public void setNewObjectCount(int newObjectCount) {
        usedField(NEW_OBJECT_COUNT, newObjectCount > 1 ?
                newObjectCount : null);
        if (newObjectCount < 1) {
            throw new IllegalArgumentException("Cannot set " +
                    "the new object count to less than one: " + 
                    newObjectCount);
        }
        this.newObjectCount = newObjectCount;
    }
    
    Queries kind() {
        return kind;
    }

    /**
     * Set the object used for this query.  Used with Queries.EXISTING_OBJECTS,
     * Queries.PROTOTYPE, Queries.COMPLEX.
     * 
     * @param object The object.  In the case of Queries.COMPLEX, must be an
     * instance of QueryElement, such as FieldQueryElement.
     */
    public void setObject(Object object) {
        if (object != null && this.objects != null) {
            throw new IllegalArgumentException ("Use setObject() or " +
                    "setObjects() but not both, and do not call more " +
                    "than once");
        }
        if (this.kind == Queries.COMPLEX) {
            if (object != null && !(object instanceof QueryElement)) {
                throw new IllegalArgumentException ("In the case of " +
                        "Queries.CUSTOM, arguments to setObject must be " +
                        "instances of QueryElement");
            }
        }
        usedField(OBJECT, object);
        this.object = object;
    }

    /**
     * Set the policy for what to do if the query fails.
     * 
     * @param policy The policy
     */
    public void setPolicy(LoadFailurePolicy policy) {
        usedField (POLICY, policy);
        this.policy = policy;
    }

    /**
     * Set the type of the query.  Generally this is called when the query builder is
     * constructed.
     * //PENDING: make private
     * @param type
     */
    public void setType(Class<T> type) {
        usedField (TYPE, type);
        this.type = type;
    }

    /**
     * Set the unique id of a single object that should be looked up by the
     * query.
     * 
     * @param uid A unique id
     */
    public void setUid(long uid) {
        if (uid != -1L && uids != null && !uids.isEmpty()) {
            throw new IllegalStateException("Cannot call both setUid() and " +
                    "setUids(), use one or the other");
        }
        usedField (UID, uid == -1L ? null : uid);
        this.uid = uid;
    }

    /**
     * A universal unique id of a single object that should be looked up by
     * the query.
     * @param uuid A universal unique string id identifying one object
     */
    public void setUuid(String uuid) {
        if (uuid != null && uuids != null && !uuids.isEmpty()) {
            throw new IllegalArgumentException("Cannot call both setUuid() and " +
                    "setUuids(), use one or the other");
        }
        usedField (UUID, uuid);
        this.uuid = uuid;
    }
    
    /**
     * Set a comparator for sorting the results of the query.  Only meaningful
     * if you are going to call QueryBuilder.multi() to create a model of a
     * collection of objects.
     * 
     * @param comparator A comparator.
     */
    public void setComparator(Comparator<T> comparator) {
        this.comparator = comparator;
    }
    
    /**
     * Set a collection of unique ids for a collection of objects that should
     * be found by the query.  Used by Queries.UID.
     * @param uids Some unique ids
     */
    public void setUids(Collection<Long> uids) {
        if (uid != -1L) {
            throw new IllegalArgumentException("Cannot call both setUid() and " +
                    "setUids(), use one or the other");
        }
        usedField(UID, uids);
        this.uids = uids;
    }
    
    /**
     * Set a collection of universal unique ids that should be looked up to
     * produce the result of the query.  Used by Queries.UUID.
     * @param uuids A collection of universal unique ids
     */
    public void setUUids(Collection<String> uuids) {
        usedField(UUID, uuids == null || uuids.isEmpty() ? null : uuids);
        this.uuids = uuids;
    }
    
    /**
     * Set a collection of objects you want a model for.  Only meaningful if
     * you are going to call QueryBuilder.multi(). 
     * Used by Queries.EXISTING_OBJECTS.
     * @param objects
     */
    public void setObjects(Collection<T> objects) {
        if (object != null && objects != null) {
            throw new IllegalArgumentException ("Use setObject() or " +
                    "setObjects() but not both");
        }
        usedField (OBJECT, objects == null || objects.isEmpty() ? null : objects);
        this.objects = objects;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        @SuppressWarnings("unchecked")
        final ModelBuilder<T, P> other = ( ModelBuilder<T, P>) obj;
        if (this.kind != other.kind && (this.kind == null || !this.kind.equals(other.kind))) {
            return false;
        }
        if (this.usedFields != other.usedFields && (this.usedFields == null || !this.usedFields.equals(other.usedFields))) {
            return false;
        }
        if (this.uuid != other.uuid && (this.uuid == null || !this.uuid.equals(other.uuid))) {
            return false;
        }
        if (this.policy != other.policy && (this.policy == null || !this.policy.equals(other.policy))) {
            return false;
        }
        if (this.factory != other.factory && (this.factory == null || !this.factory.equals(other.factory))) {
            return false;
        }
        if (this.object != other.object && (this.object == null || !this.object.equals(other.object))) {
            return false;
        }
        if (this.fieldType != other.fieldType && (this.fieldType == null || !this.fieldType.equals(other.fieldType))) {
            return false;
        }
        if (this.fieldValue != other.fieldValue && (this.fieldValue == null || !this.fieldValue.equals(other.fieldValue))) {
            return false;
        }
        if (this.fieldName != other.fieldName && (this.fieldName == null || !this.fieldName.equals(other.fieldName))) {
            return false;
        }
        if (this.newObjectCount != other.newObjectCount) {
            return false;
        }
        if (this.customQuery != other.customQuery && (this.customQuery == null || !this.customQuery.equals(other.customQuery))) {
            return false;
        }
        if (this.customQueryArg != other.customQueryArg && (this.customQueryArg == null || !this.customQueryArg.equals(other.customQueryArg))) {
            return false;
        }
        if (this.uid != other.uid) {
            return false;
        }
        return !(this.type != other.type && (this.type == null || !this.type.equals(other.type)));
    }

    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.kind != null ? this.kind.hashCode() : 0);
        hash = 67 * hash + (this.usedFields != null ? this.usedFields.hashCode() : 0);
        hash = 7 * hash + (this.uuid != null ? this.uuid.hashCode() : 0);
        hash = 67 * hash + (this.policy != null ? this.policy.hashCode() : 0);
        hash = 11 * hash + (this.factory != null ? this.factory.hashCode() : 0);
        hash = 31 * hash + (this.object != null ? this.object.hashCode() : 0);
        hash = 67 * hash + (this.fieldType != null ? this.fieldType.hashCode() : 0);
        hash = 67 * hash + (this.fieldValue != null ? this.fieldValue.hashCode() : 0);
        hash = 67 * hash + (this.fieldName != null ? this.fieldName.hashCode() : 0);
        hash = 67 * hash + this.newObjectCount;
        hash = 67 * hash + (this.customQuery != null ? this.customQuery.hashCode() : 0);
        hash = 67 * hash + (this.customQueryArg != null ? this.customQueryArg.hashCode() : 0);
        hash = 67 * hash + (int) (this.uid ^ (this.uid >>> 32));
        hash = 67 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }
}
