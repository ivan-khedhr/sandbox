/*
 * Ext JS Library 2.0 Beta 1
 * Copyright(c) 2006-2007, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */

Ext.MessageBox=function(){var R,B,N,Q;var G,J,P,A,K,M,H,F;var O,S,L,C="";var D=function(U){R.hide();Ext.callback(B.fn,B.scope||window,[U,S.dom.value],1)};var T=function(){if(B&&B.cls){R.el.removeClass(B.cls)}K.reset()};var E=function(W,U,V){if(B&&B.closable!==false){R.hide()}if(V){V.stopEvent()}};var I=function(U){var W=0;if(!U){O["ok"].hide();O["cancel"].hide();O["yes"].hide();O["no"].hide();return W}R.footer.dom.style.display="";for(var V in O){if(typeof O[V]!="function"){if(U[V]){O[V].show();O[V].setText(typeof U[V]=="string"?U[V]:Ext.MessageBox.buttonText[V]);W+=O[V].el.getWidth()+15}else{O[V].hide()}}}return W};return{getDialog:function(U){if(!R){R=new Ext.Window({autoCreate:true,title:U,resizable:false,constrain:true,constrainHeader:true,minimizable:false,maximizable:false,stateful:false,modal:true,shim:true,buttonAlign:"center",width:400,height:100,minHeight:80,plain:true,footer:true,closable:true,close:function(){if(B&&B.buttons&&B.buttons.no&&!B.buttons.cancel){D("no")}else{D("cancel")}}});O={};var V=this.buttonText;O["ok"]=R.addButton(V["ok"],D.createCallback("ok"));O["yes"]=R.addButton(V["yes"],D.createCallback("yes"));O["no"]=R.addButton(V["no"],D.createCallback("no"));O["cancel"]=R.addButton(V["cancel"],D.createCallback("cancel"));O["ok"].hideMode=O["yes"].hideMode=O["no"].hideMode=O["cancel"].hideMode="offsets";R.render(document.body);R.getEl().addClass("x-window-dlg");N=R.mask;G=R.body.createChild({html:"<div class=\"ext-mb-icon\"></div><div class=\"ext-mb-content\"><span class=\"ext-mb-text\"></span><br /><input type=\"text\" class=\"ext-mb-input\" /><textarea class=\"ext-mb-textarea\"></textarea></div>"});H=Ext.get(G.dom.firstChild);var W=G.dom.childNodes[1];J=Ext.get(W.firstChild);P=Ext.get(W.childNodes[2]);P.enableDisplayMode();P.addKeyListener([10,13],function(){if(R.isVisible()&&B&&B.buttons){if(B.buttons.ok){D("ok")}else{if(B.buttons.yes){D("yes")}}}});A=Ext.get(W.childNodes[3]);A.enableDisplayMode();K=new Ext.ProgressBar({renderTo:G});G.createChild({cls:"x-clear"})}return R},updateText:function(X){if(!R.isVisible()&&!B.width){R.setSize(this.maxWidth,100)}J.update(X||"&#160;");var V=C!=""?(H.getWidth()+H.getMargins("lr")):0;var Z=J.getWidth()+J.getMargins("lr");var W=R.getFrameWidth("lr");var Y=R.body.getFrameWidth("lr");if(Ext.isIE&&V>0){V+=3}var U=Math.max(Math.min(B.width||V+Z+W+Y,this.maxWidth),Math.max(B.minWidth||this.minWidth,L||0));if(B.prompt===true){S.setWidth(U-V-W-Y)}if(B.progress===true||B.wait===true){K.setSize(U-V-W-Y)}R.setSize(U,"auto").center();return this},updateProgress:function(V,U,W){K.updateProgress(V,U);if(W){this.updateText(W)}return this},isVisible:function(){return R&&R.isVisible()},hide:function(){if(this.isVisible()){R.hide();T()}return this},show:function(X){if(this.isVisible()){this.hide()}B=X;var Y=this.getDialog(B.title||"&#160;");Y.setTitle(B.title||"&#160;");var U=(B.closable!==false&&B.progress!==true&&B.wait!==true);Y.tools.close.setDisplayed(U);S=P;B.prompt=B.prompt||(B.multiline?true:false);if(B.prompt){if(B.multiline){P.hide();A.show();A.setHeight(typeof B.multiline=="number"?B.multiline:this.defaultTextHeight);S=A}else{P.show();A.hide()}}else{P.hide();A.hide()}S.dom.value=B.value||"";if(B.prompt){Y.focusEl=S}else{var W=B.buttons;var V=null;if(W&&W.ok){V=O["ok"]}else{if(W&&W.yes){V=O["yes"]}}if(V){Y.focusEl=V}}this.setIcon(B.icon);L=I(B.buttons);K.setVisible(B.progress===true||B.wait===true);this.updateProgress(0,B.progressText);this.updateText(B.msg);if(B.cls){Y.el.addClass(B.cls)}Y.proxyDrag=B.proxyDrag===true;Y.modal=B.modal!==false;Y.mask=B.modal!==false?N:false;if(!Y.isVisible()){document.body.appendChild(R.el.dom);Y.setAnimateTarget(B.animEl);Y.show(B.animEl)}Y.on("show",function(){if(U===true){Y.keyMap.enable()}else{Y.keyMap.disable()}});if(B.wait===true){K.wait(B.waitConfig)}return this},setIcon:function(U){if(U&&U!=""){H.removeClass("x-hidden");H.replaceClass(C,U);C=U}else{H.replaceClass(C,"x-hidden");C=""}return this},progress:function(W,V,U){this.show({title:W,msg:V,buttons:false,progress:true,closable:false,minWidth:this.minProgressWidth,progressText:U});return this},wait:function(W,V,U){this.show({title:V,msg:W,buttons:false,closable:false,wait:true,modal:true,minWidth:this.minProgressWidth,waitConfig:U});return this},alert:function(X,W,V,U){this.show({title:X,msg:W,buttons:this.OK,fn:V,scope:U});return this},confirm:function(X,W,V,U){this.show({title:X,msg:W,buttons:this.YESNO,fn:V,scope:U,icon:this.QUESTION});return this},prompt:function(Y,X,W,V,U){this.show({title:Y,msg:X,buttons:this.OKCANCEL,fn:W,minWidth:250,scope:V,prompt:true,multiline:U});return this},OK:{ok:true},YESNO:{yes:true,no:true},OKCANCEL:{ok:true,cancel:true},YESNOCANCEL:{yes:true,no:true,cancel:true},INFO:"ext-mb-info",WARNING:"ext-mb-warning",QUESTION:"ext-mb-question",ERROR:"ext-mb-error",defaultTextHeight:75,maxWidth:600,minWidth:100,minProgressWidth:250,buttonText:{ok:"OK",cancel:"Cancel",yes:"Yes",no:"No"}}}();Ext.Msg=Ext.MessageBox;
