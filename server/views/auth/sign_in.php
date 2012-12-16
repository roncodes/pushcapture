<div class="comment-form">
		<h2>Sign in</h2>
		<form method="post" action="<?=base_url('auth/sign_in')?>" class="ajax_form" name="sign_in">
		 
			<div class="row">
				<input type="text" name="identity" id="identity" value="" class="inputtext required" placeholder="Username" />
			</div>
			
			<div class="row">
				<input type="password" name="password" id="password" class="inputtext required" placeholder="Password" style="" />
			</div>
			
			<div class="row">
				<input type="submit" value="Sign In" id="send" style="margin:10px 0 0 0px;" class="btn-submit" />
			</div>
		
			<div class="clear"></div>
			
		</form>
	</div>