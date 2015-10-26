
 
function showTips( css,message)
 {
 
         if($('#GTip').length==0)
	    {  
	         var tip = "<div id='GTip' style=''><a id='tip_icon'/><label style='margin-left:40px;'>"+message+"</label></div>"
	         var h = $(document).height();
			 var w = $(document).width();
		     $('body').append($(tip));
		     $('#GTip').css({top:(h-40)/3,left:(w-300)/2});
		     
	    }else
	    {
	         $('#GTip').removeAttr('class');
	         $('#tip_icon').removeAttr('class');
	    
	    }
	    
	         $('#GTip').addClass(css.tip);
		     $('#tip_icon').addClass(css.icon);
		     $('#GTip').fadeIn("slow",function(){
		      window.setTimeout(function(){
		         $('#GTip').fadeOut("slow",function(){$('#GTip').remove()});
		      },2000);
		    });
 
}

function prepareShowProcess(css,message)
 {
        if($('#ProcessDialog').length==0)
	    { 
	     var tip = "<div id='ProcessDialog' style=''><a id='ProcessIcon'/><label style='margin-left:40px;'>"+message+"</label></div>"
         var h = $(document).height();
		 var w = $(document).width();
	     $('body').append($(tip));
	     $('#ProcessDialog').addClass(css.tip);
	     $('#ProcessIcon').addClass(css.icon);
	     $('#ProcessDialog').css({top:(h-40)/3,left:(w-300)/2});
	     $('#ProcessDialog').fadeIn("slow");
	    }else
	    {
	    
	      $('#ProcessDialog').find('label').text(message);
	      $('#ProcessDialog').fadeIn("slow");
	    }
	    
        $('.panel').css('z-index','997');
}
function hideProcess()
 {
	     $('#ProcessDialog').hide();
	     if($('.gdialog:visible').length==0)
	     {
	       $('#global_mask').fadeOut("slow");
	     }
	     $('.panel').css('z-index','1000');
}
function showSTip(message)
 {
        showTips({tip:'tip_green',icon:'icon_success'},message);
}
function showHTip(message)
{
        showTips({tip:'tip_blue',icon:'icon_hint'},message);
}
function showProcess(message)
{        
        prepareShowProcess({tip:'tip_process',icon:'icon_loading_small'},message);
        
        var h = $(document).height();
	    $('#global_mask').css('height',h);
        $('#global_mask').fadeIn("slow");
}
function showETip(message)
 {
        showTips({tip:'tip_red',icon:'icon_error'},message);
}

function fadeInTips( css,message)
{
		$('#tips').removeClass();
		$('#tips').addClass(css);
	    $('#tips').html(message);
	    $('#tips').fadeIn("slow");
}

function isEmail(str)
{
    var reg = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
	if(reg.test(str)){
	  return true;
	}else{
	 return false;
	}
}

 
function checkImageType(filename)
{
	var postf=filename.substring(filename.lastIndexOf('.')+1,filename.length).toLowerCase();
	if(postf!='jpg'&&postf!='png'&&postf!='bmp'&& postf!='jpeg'&& postf!='gif')
	{
	  return false;
	}
	$('#postf').val(postf);
	return true;
}
 


function prepareShowDialog(dialog)
 {
     var h = $(document).height();
	 var w = $(document).width();
	 var mheight = dialog.height();
	 var mwidth = dialog.width();
     dialog.css({top:150,left:(w-mwidth)/2});
     $('#global_mask').css('height',h);
     $('#global_mask').fadeIn();
     $("body").attr("unselectable","no");
     $("body").attr("onselectstart","return false;");
     dialog.find(".panel-heading").mousedown(function(e) 
            {  
                var offset = $(this).offset(); 
                var x = e.pageX - offset.left; 
                var y = e.pageY - offset.top; 
                $(document).bind("mousemove",function(ev) 
                {  
                    var _x = ev.pageX - x; 
                    var _y = ev.pageY - y; 
                   if(_x<=0)
                   {
                       _x=0;
                   }  
                   if(_y <=0 )
                   {
                       _y=0;
                   }
                   if(_x >= (w - mwidth))
                   {
                       _x=(w - mwidth);
                   } 
                   if( _y >= (h - mheight))
                   {
                      _y = (h - mheight);
                   } 
                   
                   dialog.css({left:_x+"px",top:_y+"px"});  
                });  
                  
            });  
              
            $(document).mouseup(function()  
            {  
                dialog.css("cursor","default");  
                $(this).unbind("mousemove");  
            })  
            
            
}


function doShowDialog(dialogId,animate){
     
     
     var dialog = $('#'+dialogId);
     prepareShowDialog(dialog);
     if(animate==undefined)
     {
        /*$('#'+dialogId).fadeIn();
        return;*/
        animate  ='slide_in_left';
     }
     
     startDialogAnimation(dialog,animate);
   
}

function doHideDialog(dialogId,animate){

    var dialog = $('#'+dialogId);
    
    if($('.gdialog:visible').length==1)
	{
		 $('#global_mask').fadeOut();
	}
    if(animate==undefined)
     {
        /*dialog.fadeOut();
        return;*/
        animate  ='slide_out_right';
     }
     
     startDialogAnimation(dialog,animate);
   
   
}

//播放动画
function startDialogAnimation(dialog,animate)
{

   switch(animate)
   {
   
     //显示动画-----------------------------
     case 'slide_in_top':
     
          var top = dialog.css('top');
	      dialog.css('top',"0px");
	      dialog.animate({top:top,opacity: 'toggle'}, 500 );
          break;

     case 'slide_in_left':
     
          var left = dialog.css('left');
	      dialog.css('left',"0px");
	      dialog.animate({left:left,opacity: 'toggle'}, 500 );
          break;

     case 'size_in_size':
     
          var height = dialog.css('height');
          var width = dialog.css('width');
	      dialog.css('height',"0px");
	      dialog.css('width',"0px");
	      dialog.animate({height:height,width:width,opacity: 'toggle'}, 500 );
          break;
          
    //隐藏动画-----------------------------
    
     case 'slide_out_top':
     
          var height = dialog.css('height');
	      dialog.animate({top:'-'+height,opacity: 'toggle'}, 500 ,function(){dialog.css({top:'0px',left:'0px'});});
          break;

     case 'slide_out_right':
     
          dialog.animate({left:$(document).width(),opacity: 'toggle'}, 500,function(){dialog.css({top:'0px',left:'0px'});});
          break;

     case 'slide_out_size':
     
          var width =dialog.css('width');
          var height =dialog.css('height');
	      dialog.animate({height:0,width:0,opacity: 'toggle'}, 500,function(){dialog.css({top:'0px',left:'0px',height:height,width:width});} );
          break;
   }
}


function doShowConfirm(setting){

     if($('#ConfirmDialog').length==0)
	 {
	     var dialog = "<div class='panel panel-primary gdialog' id='ConfirmDialog' style='display: none;position: absolute;min-width:400px;' ><div>";
	     var head = "<div class='panel-heading'><b>提示</b><a class='close'  onclick='doHideConfirm()'>&times;</a></div>";
	     var body = "<div class='panel-body'></div>";
	     var foot = "<div class='panel-footer' style='padding:5px 10px;'><div  style='text-align: right;'><button type='button' class='btn btn-default confirm-cancel' >取消</button><button type='button' class='btn btn-success confirm-ok' style='margin-left: 15px;' >确定</button></div></div>";
	     $('body').append($(dialog));
	     $(head).appendTo($('#ConfirmDialog'));
	     $(body).appendTo($('#ConfirmDialog'));
	     $(foot).appendTo($('#ConfirmDialog'));
     }
     $('#ConfirmDialog').find('.panel-body').html(setting.hint);
     
     if(setting.onCancel!=undefined && jQuery.isFunction(setting.onCancel))
     {
       $('#ConfirmDialog').find('.panel-footer').find(".confirm-cancel").one('click',setting.onCancel);
     }else
     {
        $('#ConfirmDialog').find('.panel-footer').find(".confirm-cancel").one('click',function(){doHideConfirm();});
     }
     
     $('#ConfirmDialog').find('.panel-footer').find(".confirm-ok").one('click',setting.onConfirm);
     
     doShowDialog('ConfirmDialog','slide_in_top');
}

function doHideConfirm(){
    doHideDialog('ConfirmDialog','slide_out_top');
}
//--------------           dialog -----------------------------------------------------------------------------------


function gotoPage(number)
{
    var intreg =  /^\d+$/;
    if(intreg.test(number))
    {
      $('#currentPage').val(number);
	  document.getElementById("searchForm").submit();
    }
}


function getDateTime(t) {     
    //return new Date(parseInt(t)).toLocaleString().substr(0,17)
    //return new Date(parseInt(t)).pattern("yyyy-MM-dd HH:mm:ss");
    return new Date(parseInt(t)).toLocaleString().replace(/年|月/g, "-").replace(/日/g, " ");
}   