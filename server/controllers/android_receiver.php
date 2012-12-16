<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Android_Receiver extends CI_Controller {

	public function __construct()
	{
		parent::__construct();
	}
	
	public function index()
	{
		if(count($_FILES)) {
			/* Configure image upload */
			$config['upload_path'] = 'public/android/encoding';
			$config['allowed_types'] = 'gif|jpg|png';
			$config['max_size']	= '10000';
			$config['max_width']  = '10240';
			$config['max_height']  = '7680';
			
			$this->load->library('upload', $config);
			
			/* Upload image from stream */
			$this->upload->do_upload('stream');
		}
	}
	
}