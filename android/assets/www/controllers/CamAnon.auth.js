CamAnon.controller.auth = {
	
	core : CamAnon,
	
	login : function() {
		//if(this.core.is_view('login')) {
			$.post(this.core.base_url + 'index.php/auth/login', { identity : $('#email').val(), password : $('#password').val() }, function(data) {
				if(data == 'success') {
					CamAnon.init();
				} else if (data == 'fail') {
					CamAnon.controller.auth._alert('Login attempt failed', 'error');
				} else {
					CamAnon.controller.auth._alert(data, 'error');
				}
			});
		//}
	},
	
	logout : function() {
		$.post(this.core.base_url + 'index.php/auth/logout', function() {
			CamAnon.init();
		});
	},
	
	register : function() {
		$.post(this.core.base_url + 'index.php/auth/create_user', {username : $('#username').val(), email : $('#email').val(), first_name : $('#first_name').val(), last_name : $('#last_name').val(), password : $('#password').val(), password_confirm : $('#password_confirm').val() }, function(data) {
			if(data == 'success') {
				CamAnon.controller.auth._alert('Account creation successful, you may now login and begin streaming', 'success');
				setTimeout(function() { CamAnon.init(); }, 1500);
			} else {
				data = $.parseJSON(data);
				CamAnon.controller.auth._alert(data.message, 'error');
				$('#username').val(data.username.value);
				$('#email').val(data.email.value);
				$('#first_name').val(data.first_name.value);
				$('#last_name').val(data.last_name.value);
			}
		});
	},
	
	_alert : function(message, type) {
		if(type == undefined) {
			type = 'info';
		}
		$('.alert').addClass('alert-' + type);
		$('.alert').html(message);
		$('.alert').fadeIn();
		setTimeout(function() { $('.alert').fadeOut(); $('.alert').removeClass('alert-' + type); }, 3500);
	},
	
}