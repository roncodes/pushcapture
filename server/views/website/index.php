<h1>Live Streams Now</h1>
<script type="text/javascript" src="<?=base_url('public/js/jquery.isotope.min.js')?>"></script>
    <script type="text/javascript">
		jQuery(document).ready(function($) {
		
			$('#big_container .media_block img').each(function(index) {
				var item_height = $(this).attr("height");
				$(this).parent().parent().css("height",item_height);
			});
			
			$('#big_container').isotope({
			itemSelector : '.item',
			layoutMode : 'masonry',
			sortBy : 'date',
			sortAscending : false,
			getSortData : {
				date : function ( $elem ) {
					return $elem.find('.date').text(); // Date format should be [Y-m-d H:i]
				},
				featured : function ( $elem ) {
					return $elem.attr('data-featured');
			  	},
				rates : function( $elem ) {
					return parseInt( $elem.attr('data-rates'), 10 );
			  	},
				comments : function( $elem ) {
					return parseInt( $elem.attr('data-comments'), 10 );
			  	}
			}
			});
		  
			$('#sort-by li a').click(function(){
				var $this = $(this);
				if ($(this).parent().hasClass('selected') ) {
				  return false;
				}
				var $optionSet = $this.parents();
				$optionSet.find('.selected').removeClass('selected');
       			$this.addClass('selected');
								
			  	var sortName = $(this).attr('href').slice(1);
			  	$('#big_container').isotope({ sortBy : sortName });
			  	return false;
			});
		});
	</script>
	<?php
	$streams = range(1, 4);
	?>
	<div id="big_container">
	<?php foreach($streams as $stream) { ?>
	<div class="item item-image" data-featured="1" data-rates="30" data-comments="15">
		<div class="media_block preloader">
		<img src="http://placehold.it/220x205" width="220" height="205" alt="">
		<a href="<?=base_url('website/watch/3820382')?>" class="zoom">&nbsp;</a>
		<a href="<?=base_url('website/watch/3820382')?>" class="name">streaming now...</a>
		<a href="<?=base_url('website/watch/3820382')?>" class="zoom">&nbsp;</a>
		</div>
	</div>
	<?php } ?>
	</div>