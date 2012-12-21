var PushCapture = { 
		init : function () { 
			console.log('Attempting to call the PushCapture plugin');
			return cordova.exec(function() {
				alert('PushCapture was successful');
			}, function(error) {
				alert(error);
			}, "PushCapture", "capture", []);
		} 
};