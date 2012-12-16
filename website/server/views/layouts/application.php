<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="author" content="Ronald A. Richardson" />
<meta name="description" content="" />
<meta name="keywords" content="" />
<title>Cam Anon</title>

<link href="<?=base_url('public/style.css')?>" media="screen" rel="stylesheet" type="text/css" />
<link href="<?=base_url('public/custom.css')?>" media="screen" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="<?=base_url('public/js/jquery.min.js')?>"></script>
<script type="text/javascript" src="<?=base_url('public/js/preloadCssImages.js')?>"></script>
<script type="text/javascript" src="<?=base_url('public/js/jquery.easing.1.3.js')?>"></script>
<script type="text/javascript" src="<?=base_url('public/js/jquery.tools.min.js')?>"></script>
<script type="text/javascript" src="<?=base_url('public/js/general.js')?>"></script>

<script type="text/javascript" src="<?=base_url('public/js/slides.min.jquery.js')?>"></script>

<script type="text/javascript" src="<?=base_url('public/js/jquery.preloadify.min.js')?>"></script>
<script type="text/javascript">
	jQuery(document).ready(function($) {
		$(".preloader").preloadify({ imagedelay:400 });
		setTimeout(function() {$('#monolog').slideUp('slow'); }, 1500);
	});
</script>

<!--[if lt IE 9]><script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script><![endif]-->
<!--[if IE 7]><link rel="stylesheet" type="text/css" href="css/ie.css" /><![endif]-->

</head>

<body>
<?=showflashmsg()?>
<div class="body_wrap">

<div class="header">
    
    <div class="topmenu">
	
		<div class="widget-container widget_search" style="float:right;">
			<form method="get" id="searchform">
				<div>
				<input class="input_search" name="s" id="s2" value="Search ..." onfocus="if (this.value == 'Search ...') {this.value = '';}" onblur="if (this.value == '') {this.value = 'Search ...';}" type="text">
				<input id="searchsubmit" class="btn-submit " value="Submit" type="submit">
				</div>
			</form>
		</div>
		
		<ul class="dropdown" style="float:right">
			<li class="menu-item-home"><a href="<?=base_url()?>"><span>Cam Anon</span></a></li>
		</ul>
	</div>
	
    
    <div class="header_social">
		<?php if(!logged_in()) { ?>
    	<a href="<?=base_url('auth/sign_in')?>" class="button_link btn_blue" style="opacity: 1;"><span>Sign In</span></a>
		<?php } else { ?>
		<div class="topmenu" style="margin-left:0px;float:none;top:0px;">
			<ul class="dropdown">
				<li><a href="<?=base_url('website/channel/'.$user->username)?>"><span><?=$user->username?></span></a></a>
					<ul style="text-align:left;">
						<li><a href="<?=base_url('website/dashboard')?>"><span>Dashboard</span></a></li>
						<li><a href="<?=base_url('website/settings')?>"><span>Settings</span></a></li>
						<?php if(is_admin()){ ?><li><a href="<?=base_url('admin')?>"><span>Admin</span></a></li><?php } ?>
						<li><a href="<?=base_url('auth/logout')?>"><span>Logout</span></a></li>
					</ul>
				</li>
			</ul>
		</div>
		<?php } ?>
    </div>
        
</div>
<!--/ header -->

<!-- middle -->
<div class="middle middle-container">
	
    <!-- content -->
    <div class="content_wrapper">
    <div class="content">
		<div id="big_container">
			<?=$yield?>
		</div>
	</div>
	</div>
</div>
<!-- footer -->
<div class="footer_top"></div>
<div class="footer">
	
    <div class="f_col_1">
    	
        <div class="widget-container widget_categories">
            <h3>LINKS</h3>
            <ul>
                <li><a href="#">Lorem Ipsum</a></li>
				<li><a href="#">Lorem Ipsum</a></li>
				<li><a href="#">Lorem Ipsum</a></li>
				<li><a href="#">Lorem Ipsum</a></li>
				<li><a href="#">Lorem Ipsum</a></li>
            </ul>
        </div>
        
    </div>
    
    <div class="f_col_1">
    	<div class="widget-container widget_categories">
            <h3>LINKS</h3>
            <ul>
                <li><a href="#">Lorem Ipsum</a></li>
				<li><a href="#">Lorem Ipsum</a></li>
				<li><a href="#">Lorem Ipsum</a></li>
				<li><a href="#">Lorem Ipsum</a></li>
				<li><a href="#">Lorem Ipsum</a></li>
            </ul>
        </div>
    </div>
    
    <div class="f_col_1">
    	<div class="widget-container widget_categories">
            <h3>LINKS</h3>
            <ul>
                <li><a href="#">Lorem Ipsum</a></li>
				<li><a href="#">Lorem Ipsum</a></li>
				<li><a href="#">Lorem Ipsum</a></li>
				<li><a href="#">Lorem Ipsum</a></li>
				<li><a href="#">Lorem Ipsum</a></li>
            </ul>
        </div>
    </div>
    
    <div class="f_col_2">
    	<div class="widget-container widget_categories">
            <h3>LINKS</h3>
            <ul>
                <li><a href="#">Lorem Ipsum</a></li>
				<li><a href="#">Lorem Ipsum</a></li>
				<li><a href="#">Lorem Ipsum</a></li>
				<li><a href="#">Lorem Ipsum</a></li>
				<li><a href="#">Lorem Ipsum</a></li>
            </ul>
        </div>
    </div>
    
    <div class="f_col_2">
    	<div class="widget-container widget_categories">
            <h3>ABOUT US</h3>
            <ul>
                <li><a href="#">Lorem Ipsum</a></li>
				<li><a href="#">Lorem Ipsum</a></li>
				<li><a href="#">Lorem Ipsum</a></li>
				<li><a href="#">Lorem Ipsum</a></li>
				<li><a href="#">Lorem Ipsum</a></li>
            </ul>
        </div>
    </div>
    
    <div class="clear"></div>
    
    <div class="footer_social">
        <ul>
            <li><img src="<?=base_url('public/images/temp/social_fb_like.png')?>" width="71" height="20" alt=""></li>
            <li><img src="<?=base_url('public/images/temp/social_tweet.png')?>" width="96" height="20" alt=""></li>
            <li><img src="<?=base_url('public/images/temp/social_google_plus.png')?>" width="65" height="20" alt=""></li>
        </ul>
    </div>
    
</div>
<div class="footer_bottom">
	
    <div class="botmenu">
    	<ul>
        	<li><a href="#">Lorem Ipsum</a></li>
			<li><a href="#">Lorem Ipsum</a></li>
			<li><a href="#">Lorem Ipsum</a></li>
			<li><a href="#">Lorem Ipsum</a></li>
			<li><a href="#">Lorem Ipsum</a></li>
			<li><a href="#">Lorem Ipsum</a></li>
			<li><a href="#">Lorem Ipsum</a></li>
        </ul>
    </div>
    
    <div class="copyright">
   	&copy; Copyright <?=date('Y')?> <a href="<?=base_url()?>">Cam Anon</a>
    </div>
    
</div>
	</body>
</html>