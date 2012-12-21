CamAnon.controller.app = {
	
	core : CamAnon,
	
	dashboard : function() {
		// do code
	},
	
	capture : {
		
		init : function() {
			navigator.device.capture.captureVideo(this.success, this.error, {limit : 1});
		},
		
		success : function() {
			navigator.notification.alert('Video captured!');
		},
		
		error : function() {
			navigator.notification.alert('Error code: ' + error.code, null, 'Capture Error');
		}
	},
	
}