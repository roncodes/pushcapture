jQuery(document).ready(function($) {

 		$.preloadCssImages();
	
		$(".dropdown ul").parent("li").addClass("parent");
		$(".dropdown li:first-child, .pricing_box li:first-child").addClass("first");
		$(".dropdown li:last-child, .pricing_box li:last-child, #sort-by li:last-child").addClass("last");
		$(".dropdown li:only-child").removeClass("last").addClass("only");	
		$(".dropdown .current-menu-item, .dropdown .current-menu-ancestor").prev().addClass("current-prev");		

		$("ul.tabs").tabs("> .tabcontent", {
			tabs: 'li', 
			effect: 'fade'
		});
		
	$(".recent_posts li:odd").addClass("odd");
	$(".popular_posts li:odd").addClass("odd");
	$(".widget-container li:even").addClass("even");
	
// cols
	$(".row .col:first-child").addClass("alpha");
	$(".row .col:last-child").addClass("omega"); 	


// toggle content
	$(".toggle_content").hide(); 
	
	$(".toggle").toggle(function(){
		$(this).addClass("active");
		}, function () {
		$(this).removeClass("active");
	});
	
	$(".toggle").click(function(){
		$(this).next(".toggle_content").slideToggle(300,'easeInQuad');
	});

// acc menu	
	$(".acc_menu_item:not(.active)").children(".acc_menu_sub").hide();
	  	
	$(".tf_acc_menu .acc_menu_item .ico, .tf_acc_menu .acc_menu_title h3").click(function(){		
		$(".tf_acc_menu .acc_menu_item").removeClass("active");
		if(false == $(this).parent().parent().children(".acc_menu_sub").is(':visible')) {
			$(this).parent().parent().addClass("active");
			$('.tf_acc_menu .acc_menu_sub').slideUp(400,'easeInOutSine').removeClass("active");				
		}		
		$(this).parent().parent().children(".acc_menu_sub").slideToggle(400,'easeInOutSine');
	});	

// gallery
	$(".gl_col_2 .gallery-item::nth-child(2n)").addClass("nomargin");
	
// pricing
	$(".pricing_box li.price_col").css('width',$(".pricing_box ul").width() / $(".pricing_box li.price_col").size());

// buttons	
	if ($.browser.msie && $.browser.version=="7.0") {
		return false;
	} else {
		$(".button_link").hover(function(){
			$(this).stop().animate({"opacity": 0.80});
		},function(){
			$(this).stop().animate({"opacity": 1});
		});
	}
});
// scroll to top
$(function () {  
     $(window).scroll(function () {  
         if ($(this).scrollTop() != 0) {  
             $('.link-top').fadeIn();  
         } else {  
             $('.link-top').fadeOut();  
         }  
     });  
     $('.link-top').click(function () {  
         $('body,html').animate({  
             scrollTop: 0  
         },  
         1500);  
     });  
 });