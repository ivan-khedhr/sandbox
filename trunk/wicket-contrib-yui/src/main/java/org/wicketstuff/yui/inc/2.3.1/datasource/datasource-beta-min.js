/*
Copyright (c) 2007, Yahoo! Inc. All rights reserved.
Code licensed under the BSD License:
http://developer.yahoo.net/yui/license.txt
version: 2.3.1
*/
YAHOO.util.DataSource=function(B,D){if(D&&(D.constructor==Object)){for(var C in D){if(C){this[C]=D[C];}}}if(!B){return ;}if(B.nodeType&&B.nodeType==9){this.dataType=YAHOO.util.DataSource.TYPE_XML;}else{if(YAHOO.lang.isArray(B)){this.dataType=YAHOO.util.DataSource.TYPE_JSARRAY;}else{if(YAHOO.lang.isString(B)){this.dataType=YAHOO.util.DataSource.TYPE_XHR;}else{if(YAHOO.lang.isFunction(B)){this.dataType=YAHOO.util.DataSource.TYPE_JSFUNCTION;}else{if(B.nodeName&&(B.nodeName.toLowerCase()=="table")){this.dataType=YAHOO.util.DataSource.TYPE_HTMLTABLE;}else{if(YAHOO.lang.isObject(B)){this.dataType=YAHOO.util.DataSource.TYPE_JSON;}else{this.dataType=YAHOO.util.DataSource.TYPE_UNKNOWN;}}}}}}this.liveData=B;this._oQueue={interval:null,conn:null,requests:[]};var A=this.maxCacheEntries;if(!YAHOO.lang.isNumber(A)||(A<0)){A=0;}if(A>0&&!this._aCache){this._aCache=[];}this._sName="DataSource instance"+YAHOO.util.DataSource._nIndex;YAHOO.util.DataSource._nIndex++;this.createEvent("cacheRequestEvent");this.createEvent("cacheResponseEvent");this.createEvent("requestEvent");this.createEvent("responseEvent");this.createEvent("responseParseEvent");this.createEvent("responseCacheEvent");this.createEvent("dataErrorEvent");this.createEvent("cacheFlushEvent");};YAHOO.augment(YAHOO.util.DataSource,YAHOO.util.EventProvider);YAHOO.util.DataSource.TYPE_UNKNOWN=-1;YAHOO.util.DataSource.TYPE_JSARRAY=0;YAHOO.util.DataSource.TYPE_JSFUNCTION=1;YAHOO.util.DataSource.TYPE_XHR=2;YAHOO.util.DataSource.TYPE_JSON=3;YAHOO.util.DataSource.TYPE_XML=4;YAHOO.util.DataSource.TYPE_TEXT=5;YAHOO.util.DataSource.TYPE_HTMLTABLE=6;YAHOO.util.DataSource.ERROR_DATAINVALID="Invalid data";YAHOO.util.DataSource.ERROR_DATANULL="Null data";YAHOO.util.DataSource._nIndex=0;YAHOO.util.DataSource._nTransactionId=0;YAHOO.util.DataSource.prototype._sName=null;YAHOO.util.DataSource.prototype._aCache=null;YAHOO.util.DataSource.prototype._oQueue=null;YAHOO.util.DataSource.prototype.maxCacheEntries=0;YAHOO.util.DataSource.prototype.liveData=null;YAHOO.util.DataSource.prototype.dataType=YAHOO.util.DataSource.TYPE_UNKNOWN;YAHOO.util.DataSource.prototype.responseType=YAHOO.util.DataSource.TYPE_UNKNOWN;YAHOO.util.DataSource.prototype.responseSchema=null;YAHOO.util.DataSource.prototype.connMgr=null;YAHOO.util.DataSource.prototype.connXhrMode="allowAll";YAHOO.util.DataSource.prototype.connMethodPost=false;YAHOO.util.DataSource.prototype.connTimeout=0;YAHOO.util.DataSource.parseString=function(B){if(!YAHOO.lang.isValue(B)){return null;}var A=B+"";if(YAHOO.lang.isString(A)){return A;}else{return null;}};YAHOO.util.DataSource.parseNumber=function(B){var A=B*1;if(YAHOO.lang.isNumber(A)){return A;}else{return null;}};YAHOO.util.DataSource.convertNumber=function(A){return YAHOO.util.DataSource.parseNumber(A);};YAHOO.util.DataSource.parseDate=function(B){var A=null;if(!(B instanceof Date)){A=new Date(B);}else{return B;}if(A instanceof Date){return A;}else{return null;}};YAHOO.util.DataSource.convertDate=function(A){return YAHOO.util.DataSource.parseDate(A);};YAHOO.util.DataSource.prototype.toString=function(){return this._sName;};YAHOO.util.DataSource.prototype.getCachedResponse=function(H,B,G){var A=this._aCache;var D=(A)?A.length:0;var F=null;if((this.maxCacheEntries>0)&&A&&(D>0)){this.fireEvent("cacheRequestEvent",{request:H,callback:B,caller:G});for(var E=D-1;E>=0;E--){var C=A[E];if(this.isCacheHit(H,C.request)){F=C.response;A.splice(E,1);this.addToCache(H,F);this.fireEvent("cacheResponseEvent",{request:H,response:F,callback:B,caller:G});break;}}}return F;};YAHOO.util.DataSource.prototype.isCacheHit=function(A,B){return(A===B);};YAHOO.util.DataSource.prototype.addToCache=function(D,C){var A=this._aCache;if(!A){return ;}while(A.length>=this.maxCacheEntries){A.shift();}var B={request:D,response:C};A.push(B);this.fireEvent("responseCacheEvent",{request:D,response:C});};YAHOO.util.DataSource.prototype.flushCache=function(){if(this._aCache){this._aCache=[];this.fireEvent("cacheFlushEvent");}};YAHOO.util.DataSource.prototype.sendRequest=function(D,A,C){var B=this.getCachedResponse(D,A,C);if(B){A.call(C,D,B);return null;}return this.makeConnection(D,A,C);};YAHOO.util.DataSource.prototype.makeConnection=function(A,P,K){this.fireEvent("requestEvent",{request:A,callback:P,caller:K});var D=null;var L=YAHOO.util.DataSource._nTransactionId++;switch(this.dataType){case YAHOO.util.DataSource.TYPE_JSFUNCTION:D=this.liveData(A);this.handleResponse(A,D,P,K,L);break;case YAHOO.util.DataSource.TYPE_XHR:var N=this;var C=this.connMgr||YAHOO.util.Connect;var G=this._oQueue;var J=function(Q){if(Q&&(this.connXhrMode=="ignoreStaleResponses")&&(Q.tId!=G.conn.tId)){return null;}else{if(!Q){this.fireEvent("dataErrorEvent",{request:A,callback:P,caller:K,message:YAHOO.util.DataSource.ERROR_DATANULL});P.call(K,A,Q,true);return null;}else{this.handleResponse(A,Q,P,K,L);}}};var O=function(Q){this.fireEvent("dataErrorEvent",{request:A,callback:P,caller:K,message:YAHOO.util.DataSource.ERROR_DATAINVALID});if((this.liveData.lastIndexOf("?")!==this.liveData.length-1)&&(A.indexOf("?")!==0)){}P.call(K,A,Q,true);return null;};var I={success:J,failure:O,scope:this};if(YAHOO.lang.isNumber(this.connTimeout)){I.timeout=this.connTimeout;}if(this.connXhrMode=="cancelStaleRequests"){if(G.conn){if(C.abort){C.abort(G.conn);G.conn=null;}else{}}}if(C&&C.asyncRequest){var B=this.liveData;var H=this.connMethodPost;var M=(H)?"POST":"GET";var E=(H)?B:B+A;var F=(H)?A:null;if(this.connXhrMode!="queueRequests"){G.conn=C.asyncRequest(M,E,I,F);}else{if(G.conn){G.requests.push({request:A,callback:I});if(!G.interval){G.interval=setInterval(function(){if(C.isCallInProgress(G.conn)){return ;}else{if(G.requests.length>0){E=(H)?B:B+G.requests[0].request;F=(H)?G.requests[0].request:null;G.conn=C.asyncRequest(M,E,G.requests[0].callback,F);G.requests.shift();}else{clearInterval(G.interval);G.interval=null;}}},50);}}else{G.conn=C.asyncRequest(M,E,I,F);}}}else{P.call(K,A,null,true);}break;default:D=this.liveData;this.handleResponse(A,D,P,K,L);
break;}return L;};YAHOO.util.DataSource.prototype.handleResponse=function(E,C,B,D,H){this.fireEvent("responseEvent",{request:E,response:C,callback:B,caller:D,tId:H});var G=(this.dataType==YAHOO.util.DataSource.TYPE_XHR)?true:false;var F=null;var A=false;C=this.doBeforeParseData(E,C);switch(this.responseType){case YAHOO.util.DataSource.TYPE_JSARRAY:if(G&&C.responseText){C=C.responseText;}F=this.parseArrayData(E,C);break;case YAHOO.util.DataSource.TYPE_JSON:if(G&&C.responseText){C=C.responseText;}F=this.parseJSONData(E,C);break;case YAHOO.util.DataSource.TYPE_HTMLTABLE:if(G&&C.responseText){C=C.responseText;}F=this.parseHTMLTableData(E,C);break;case YAHOO.util.DataSource.TYPE_XML:if(G&&C.responseXML){C=C.responseXML;}F=this.parseXMLData(E,C);break;case YAHOO.util.DataSource.TYPE_TEXT:if(G&&C.responseText){C=C.responseText;}F=this.parseTextData(E,C);break;default:break;}if(F){F.tId=H;F=this.doBeforeCallback(E,C,F);this.fireEvent("responseParseEvent",{request:E,response:F,callback:B,caller:D});this.addToCache(E,F);}else{this.fireEvent("dataErrorEvent",{request:E,callback:B,caller:D,message:YAHOO.util.DataSource.ERROR_DATANULL});F={error:true};}B.call(D,E,F);};YAHOO.util.DataSource.prototype.doBeforeParseData=function(B,A){return A;};YAHOO.util.DataSource.prototype.doBeforeCallback=function(B,A,C){return C;};YAHOO.util.DataSource.prototype.parseArrayData=function(A,B){if(YAHOO.lang.isArray(B)&&YAHOO.lang.isArray(this.responseSchema.fields)){var J={results:[]};var G=this.responseSchema.fields;for(var E=B.length-1;E>-1;E--){var F={};for(var C=G.length-1;C>-1;C--){var H=G[C];var I=(YAHOO.lang.isValue(H.key))?H.key:H;var D=(YAHOO.lang.isValue(B[E][C]))?B[E][C]:B[E][I];if(!H.parser&&H.converter){H.parser=H.converter;}if(H.parser){D=H.parser.call(this,D);}if(D===undefined){D=null;}F[I]=D;}J.results.unshift(F);}return J;}else{return null;}};YAHOO.util.DataSource.prototype.parseTextData=function(A,B){var N={};if(YAHOO.lang.isString(B)&&YAHOO.lang.isArray(this.responseSchema.fields)&&YAHOO.lang.isString(this.responseSchema.recordDelim)&&YAHOO.lang.isString(this.responseSchema.fieldDelim)){N.results=[];var K=this.responseSchema.recordDelim;var G=this.responseSchema.fieldDelim;var J=this.responseSchema.fields;if(B.length>0){var C=B.length-K.length;if(B.substr(C)==K){B=B.substr(0,C);}var O=B.split(K);for(var F=O.length-1;F>-1;F--){var I={};for(var D=J.length-1;D>-1;D--){var H=O[F].split(G);var E=H[D];if(E.charAt(0)=="\""){E=E.substr(1);}if(E.charAt(E.length-1)=="\""){E=E.substr(0,E.length-1);}var L=J[D];var M=(YAHOO.lang.isValue(L.key))?L.key:L;if(!L.parser&&L.converter){L.parser=L.converter;}if(L.parser){E=L.parser.call(this,E);}if(E===undefined){E=null;}I[M]=E;}N.results.unshift(I);}}}else{N.error=true;}return N;};YAHOO.util.DataSource.prototype.parseXMLData=function(A,C){var I=false;var M={};var D=(this.responseSchema.resultNode)?C.getElementsByTagName(this.responseSchema.resultNode):null;if(!D||!YAHOO.lang.isArray(this.responseSchema.fields)){I=true;}else{M.results=[];for(var F=D.length-1;F>=0;F--){var N=D.item(F);var H={};for(var E=this.responseSchema.fields.length-1;E>=0;E--){var J=this.responseSchema.fields[E];var L=(YAHOO.lang.isValue(J.key))?J.key:J;var G=null;var B=N.attributes.getNamedItem(L);if(B){G=B.value;}else{var K=N.getElementsByTagName(L);if(K&&K.item(0)&&K.item(0).firstChild){G=K.item(0).firstChild.nodeValue;}else{G="";}}if(!J.parser&&J.converter){J.parser=J.converter;}if(J.parser){G=J.parser.call(this,G);}if(G===undefined){G=null;}H[L]=G;}M.results.unshift(H);}}if(I){M.error=true;}else{}return M;};YAHOO.util.DataSource.prototype.parseJSONData=function(oRequest,oRawResponse){var oParsedResponse={};if(oRawResponse&&YAHOO.lang.isArray(this.responseSchema.fields)){var fields=this.responseSchema.fields;var bError=false;oParsedResponse.results=[];var jsonObj,jsonList;if(YAHOO.lang.isString(oRawResponse)){var isNotMac=(navigator.userAgent.toLowerCase().indexOf("khtml")==-1);if(oRawResponse.parseJSON&&isNotMac){jsonObj=oRawResponse.parseJSON();if(!jsonObj){bError=true;}}else{if(window.JSON&&JSON.parse&&isNotMac){jsonObj=JSON.parse(oRawResponse);if(!jsonObj){bError=true;}}else{try{while(oRawResponse.length>0&&(oRawResponse.charAt(0)!="{")&&(oRawResponse.charAt(0)!="[")){oRawResponse=oRawResponse.substring(1,oRawResponse.length);}if(oRawResponse.length>0){var objEnd=Math.max(oRawResponse.lastIndexOf("]"),oRawResponse.lastIndexOf("}"));oRawResponse=oRawResponse.substring(0,objEnd+1);jsonObj=eval("("+oRawResponse+")");if(!jsonObj){bError=true;}}else{jsonObj=null;bError=true;}}catch(e){bError=true;}}}}else{if(oRawResponse.constructor==Object){jsonObj=oRawResponse;}else{bError=true;}}if(jsonObj&&jsonObj.constructor==Object){try{jsonList=eval("jsonObj."+this.responseSchema.resultsList);}catch(e){bError=true;}}if(bError||!jsonList){oParsedResponse.error=true;}if(jsonList&&!YAHOO.lang.isArray(jsonList)){jsonList=[jsonList];}else{if(!jsonList){jsonList=[];}}for(var i=jsonList.length-1;i>=0;i--){var oResult={};var jsonResult=jsonList[i];for(var j=fields.length-1;j>=0;j--){var field=fields[j];var key=(YAHOO.lang.isValue(field.key))?field.key:field;var data=eval("jsonResult."+key);if(!field.parser&&field.converter){field.parser=field.converter;}if(field.parser){data=field.parser.call(this,data);}if(data===undefined){data=null;}oResult[key]=data;}oParsedResponse.results.unshift(oResult);}}else{oParsedResponse.error=true;}return oParsedResponse;};YAHOO.util.DataSource.prototype.parseHTMLTableData=function(B,C){var K=false;var L=C;var J=this.responseSchema.fields;var O={};O.results=[];for(var H=0;H<L.tBodies.length;H++){var D=L.tBodies[H];for(var F=D.rows.length-1;F>-1;F--){var A=D.rows[F];var I={};for(var E=J.length-1;E>-1;E--){var M=J[E];var N=(YAHOO.lang.isValue(M.key))?M.key:M;var G=A.cells[E].innerHTML;if(!M.parser&&M.converter){M.parser=M.converter;}if(M.parser){G=M.parser.call(this,G);}if(G===undefined){G=null;}I[N]=G;}O.results.unshift(I);}}if(K){O.error=true;}else{}return O;};YAHOO.register("datasource",YAHOO.util.DataSource,{version:"2.3.1",build:"541"});
