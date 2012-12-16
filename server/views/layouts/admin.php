<!DOCTYPE HTML> 
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:og="http://ogp.me/ns#"
      xmlns:fb="http://www.facebook.com/2008/fbml"> 
<head>
	<meta charset="utf-8"> 
	<title><?=$meta_title?></title> 
	<meta name="description" content="<?=$meta_desc?>"> 
	<meta name="keywords" content="<?=$meta_keywords?>"> 
	
	<link href="<?=base_url('public/bootstrap/css/bootstrap.min.css')?>" rel="stylesheet" media="all">
	<link href="<?=base_url('public/c/fancybox.css')?>" rel="stylesheet" media="all">
	<link href="<?=base_url('public/c/main.css')?>" rel="stylesheet" media="all">
	
	<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
	<script src="<?=base_url('public/bootstrap/js/bootstrap.min.js')?>"></script>
	<script src="<?=base_url('public/j/jquery.fancybox-1.3.1.pack.js')?>"></script>
	<script src="<?=base_url('public/j/admin.js')?>"></script>
	
	<!--[if IE]>
	  	<script src="https://html5shiv.googlecode.com/svn/trunk/html5.js"></script>
	<![endif]--> 
</head>
<body>
	<div class="navbar navbar-fixed-top">
		<div class="navbar-inner">
			<div class="container">
				<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
				</a>
				<a class="brand" href="<?=base_url()?>"><?=$settings['site_name']?></a>
				<div class="nav-collapse">
					<ul class="nav">
						<li <?php if ($this->uri->segment(2) == '') echo 'class="active"'; ?>><a href="<?=base_url('admin')?>">Dashboard</a></li>
						<li <?php if ($this->uri->segment(2) == 'users') echo 'class="active"'; ?>><a href="<?=base_url('admin/users')?>">Users</a></li>
						<li <?php if ($this->uri->segment(2) == 'groups') echo 'class="active"'; ?>><a href="<?=base_url('admin/groups')?>">Groups</a></li>
						<li <?php if ($this->uri->segment(2) == 'permissions') echo 'class="active"'; ?>><a href="<?=base_url('admin/permissions')?>">Permissions</a></li>
						<li <?php if ($this->uri->segment(2) == 'options') echo 'class="active"'; ?>><a href="<?=base_url('admin/options')?>">Options</a></li>
					</ul>
					<ul class="nav pull-right">
						<li><a href="<?=base_url()?>">View Site</a></li>
						<li><a href="<?=base_url('auth/logout')?>">Logout</a></li>
					</ul>
				</div>
			</div>
		</div>
	</div>
	<div class="container" style="margin-top: 60px;">
		<?php 
		if ( ! empty($folder_name))
		{
			if (file_exists(APPPATH.'views/'.$folder_name.'subnav.php'))
			{
				echo '<div class="subnav"><ul class="nav nav-pills">';
				$this->load->view($folder_name.'subnav.php', true);
				echo '</ul></div>';
			}
		}
		?>
		<?php echo showflashmsg(); ?>
		<?php echo $yield; ?>
	</div>
	<div class="container">
		<footer class="footer">
			<p class="pull-right"><?=$settings['site_name']?>. Copyright <?=date('Y')?>. All Rights Reserved.</p>
		</footer>
	</div>
</body>
</html>