<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Website extends MY_Controller {

	function __construct()
	{
		parent::__construct();
	}
	
	function index() 
	{
		// do code
	}
	
	function watch($id = NULL)
	{	
		$this->data['id'] = $id;
	}
	
	function channel($username = NULL)
	{
		// do code
	}
	
	function settings()
	{
		// do code
	}
	
	function dashboard()
	{
		// do code
	}
	
	function demo()
	{
		// do code
	}
	
}