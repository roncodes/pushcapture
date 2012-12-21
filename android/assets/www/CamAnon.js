/*
 * CamAnon
 * Version 1.0
 * by Ronald A. Richardson 2012
 */
 
var CamAnon = {

	controller : {},
	model : {},
	view : function(dom, view) {
		if(dom == undefined) {
			dom = '#app';
		}
		$(dom).load('views/' + view + '.html');
	},
	base_url : 'http://camanon.com/',

	init : function() {
		console.log(window.location.pathname);
		$( document ).bind( "mobileinit", function() {
			$.mobile.allowCrossDomainPages = true;
		});
		console.log('Attempting to initiate Cam Anon...');
		$.get(this.base_url + 'index.php/auth/index', function(data) {
			console.log(data);
			CamAnon.redirect(data);
		});
		return void(0);
	},
	
	/* Core Methods */
	redirect : function(view, _transition) {
		if(_transition == undefined) {
			_transition = 'none';
		}
		return $.mobile.changePage('file:///android_asset/www/views/' + view + '.html', { transition : _transition });
	},
	
	is_view : function(_view) {
		var uri = window.location.pathname.split( '/' );
		view = uri[uri.length-1].split( '.' );
		if(view[0]==_view) {
			return true;
		}
		return false;
	},
	
	popup : function(dom, message, options) {
		if(options == undefined) {
			options = {};
		}
		$('#' + dom).children('.message').html(message);
		$('#' + dom).popup('open', options);
	},
	
};
