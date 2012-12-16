<h1>You are now watching stream <?=$id?> live</h1>
<div class="video-container post-item frame_box post-detail">
	<video poster="http://content.bitsontherun.com/thumbs/nfSyO85Q-320.jpg" height="350" width="100%" controls="" >
		<source src="http://devimages.apple.com/iphone/samples/bipbop/gear1/prog_index.m3u8" type="application/vnd.apple.mpegurl">
		<source src="http://content.bitsontherun.com/videos/nfSyO85Q-27m5HpIu.webm" type="video/webm">
		<source src="http://content.bitsontherun.com/videos/nfSyO85Q-52qL9xLP.mp4" type="video/mp4">
		<p class="warning">Your browser does not support HTML5 video.</p>
	</video> 
	<h3><?=date("F j, Y, g:i a")?>
</div>
<div class="comments">
	<!-- add comment -->
			<div class="add-comment" id="addcomments" style="margin-top:0px;">
				<h2>Leave your Comment:</h2>
				
				<div class="comment-form">
				<form method="post">
				 
					<div class="row">
						<label>Name:</label>
						<input type="text" name="name" value="" class="inputtext" />
					</div>
					
					<div class="row">
						<label>Email:</label>
						<input type="text" name="email" value="" class="inputtext" />
					</div>
					
					 <div class="row">
						<label>Text:</label>
						<textarea cols="30" rows="10" name="message" class="textarea"></textarea>
						
						<input type="submit" value="SEND NOW" class="btn-submit" style="margin-top:20px;" />
					</div>
					
					<div class="clear"></div>
					
				</form>
				</div>
			</div>

			<div class="comment-list" id="comments">
			   
				<h2>4 Comments added:</h2>
				
				<ol>
		
					<li class="comment">
						<div class="comment-body">                                
						<div class="comment-avatar"><img src="<?=base_url('public/images/temp/avatar_1.gif')?>" width="53" height="53" alt="" /></div>
						<div class="comment-text">
							<div class="comment-author"><a href="#" class="link-author">Jane Smith</a>, <span class="comment-date">October 17, 2011</span></div>
							<div class="comment-entry">Libero taque earum rerum hic tenetur a sapiente delectus, ut aut reiciendis voluptatibus maiores alias consequatur aut perferendis doloribus asperiorpossimus, omnis voluptas assumenda dolor sit amet est. <a href="#addcomment" class="link-reply">reply</a></div>
						</div>
						<div class="clear"></div>
						</div>
					</li>
					
					
					
					<li class="comment">
					
						<div class="comment-body">
						<div class="comment-avatar"><img src="<?=base_url('public/images/temp/avatar_3.gif')?>" width="53" height="53" alt="" /></div>
						<div class="comment-text">
							<div class="comment-author">Alex, <span class="comment-date">October 19, 2011</span></div>
							<div class="comment-entry">For WordPress 3.2, due in the first half of 2011, we will be raising the minimum required PHP version to 5.2.  
							<a href="#addcomment" class="link-reply">reply</a>                                          </div>
						</div>
						<div class="clear"></div>
						</div>
						
						<ul class="children">
							<li class="comment">
								<div class="comment-body">
									<div class="comment-avatar"><img src="<?=base_url('public/images/temp/avatar_2.gif')?>" width="53" height="53" alt="" /></div>
								  <div class="comment-text">
										<div class="comment-author"><a href="#" class="link-author">ThemeFuse</a>, <span class="comment-date">October 20, 2011</span></div>
										<div class="comment-entry">Thanks mate, we'll consider this in the next update. Cheers! <a href="#addcomment" class="link-reply">reply</a>                                                    </div>
									</div>
									<div class="clear"></div>
								</div>                                         
						
							</li>
							
							
						</ul>
					</li>
					
					<li class="comment">
						<div class="comment-body">
						<div class="comment-avatar"><img src="<?=base_url('public/images/temp/avatar_3.gif')?>" width="53" height="53" alt="" /></div>
						<div class="comment-text">
							  <div class="comment-author"><a href="#" class="link-author">Alex</a>, <span class="comment-date">17 Jul 2010</span></div>
							<div class="comment-entry">
							Yes sure, it can be done. <a href="#addcomment" class="link-reply">reply</a>                                        </div>
						</div>
						<div class="clear"></div>
						</div>
					</li>
				</ol>
			</div>
</div>