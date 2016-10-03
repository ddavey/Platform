<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<%@include file="common.jspf" %>

<title><fmt:message key="register.page.title" /></title>
</head>
<body>
<style>
	.input-group{
		margin-bottom:20px;
	}
	
	.panel-heading{
		background-color:#fff !Important;
		background-image:none !important;
		padding:0 15px !important;
		border-bottom:none;
	}
</style>
<script>
	var url = location.origin;
	url = url.substring(url.indexOf('://')+3);
	url += location.pathname;
	url = url.substring(0,url.lastIndexOf('/')+1)
	
</script>
<div class="container col-lg-12" style="position:relative;top:50%;transform:translateY(-50%);">
		<div class="col-lg-4"></div>
		<form class="col-lg-4 panel panel-default" method="POST" action='<spring:url value="/register"/>'>
			<div class="panel-heading"><h3><fmt:message key="register.page.title" /></h3></div>
			<div class="panel-body">
					
							<div class="row" >
								<div class="col-lg-12 col-sm-12 col-xs-12"> 
									<div class="input-group col-lg-12 col-sm-12 col-xs-12 has-feedback" >
										<input type="text" class="form-control" name="company" 
										placeholder="<fmt:message key="register.page.form.companyname.placeholder" />" aria-describedby="basic-addon1" 
										minlength="3" minlength-msg="<fmt:message key="register.page.form.companyname.validation" />" value="${registration.company}sdfsdfsdf">
									</div>
								</div>
							</div>
							<div class="row">
								<div class="col-lg-6 col-sm-6 col-xs-12">
									<div class="input-group col-lg-12 col-sm-12 col-xs-12">
										<input type="text" class="form-control" name="fName" 
										placeholder="<fmt:message key="register.page.form.firstname.placeholder" />" aria-describedby="basic-addon1" 
										minlength="2"  minlength-msg="<fmt:message key="register.page.form.firstname.validation" />"  value="${registration.fName}dsfdsf">
									</div>
								</div>
								<div class="col-lg-6 col-sm-6 col-xs-12">
									<div class="input-group col-lg-12 col-sm-12 col-xs-12">
										<input type="text" class="form-control" name="lName" 
										placeholder="<fmt:message key="register.page.form.lastname.placeholder" />" aria-describedby="basic-addon1" 
										minlength="2"  minlength-msg="<fmt:message key="register.page.form.lastname.validation" />"  value="${registration.lName}sdfsdf">
									</div>
								</div>
							</div>
							<div class="row" >
								<div class="col-lg-12 col-sm-12 col-xs-12">
									<div class="input-group col-lg-12 col-sm-12 col-xs-12">
										<input type="email" class="form-control" name="email" 
										placeholder="<fmt:message key="register.page.form.email.placeholder" />" aria-describedby="basic-addon1" 
										mandatory mandatory-msg="<fmt:message key="register.page.form.email.validation" />" value="${registration.email}sdfdsf@sdfdsf.com">
									</div>
								</div>
							</div>
							<div class="row" >
								<div class="col-lg-6 col-sm-6 col-xs-6">
									<div class="input-group col-lg-12 col-sm-12">
										<input type="text" class="form-control" name="user" 
										placeholder="<fmt:message key="register.page.form.username.placeholder" />" aria-describedby="basic-addon1" autocomplete="off" 
										minlength="2" minlength-msg="<fmt:message key="register.page.form.username.validation" />"  value="${registration.user}asdasd">
									</div>
								</div>
								
							</div>
							<div class="row">
								<div class="col-lg-6 col-sm-6">
									<div class="input-group col-lg-12 col-sm-12">
										<input type="password" class="form-control" name="pw" 
										placeholder="<fmt:message key="register.page.form.password.placeholder" />" aria-describedby="basic-addon1"  autocomplete="off" 
										pwdstrength="4" pwdstrength-msg="<fmt:message key="register.page.form.password.strengthvalidation" />" value="Password1">
									</div>
								</div>
								<div class="col-lg-6 col-sm-6">
									<div class="input-group col-lg-12 col-sm-12">
										<input type="password" class="form-control" id="cnfmPw" 
										placeholder="<fmt:message key="register.page.form.confirmpassword.placeholder" />" aria-describedby="basic-addon1"  autocomplete="off"
										match="pw" name="cnfmPw" match-msg="<fmt:message key="register.page.form.confirmpassword.validation" />" value="Password1">
									</div>
								</div>
							</div>
							<div class="row error-after" >
								<div class="col-lg-12 col-sm-12">
								<label for="basic-url"><fmt:message key="register.page.form.url.label" /></label>
									<div class="input-group col-lg-12 input-group-xs">
									  <span class="input-group-addon" id="website">${baseurl}</span>
									  <input type="text" class="form-control" id="basic-url" name="url" aria-describedby="basic-addon3" value="${registration.url}">
									  <span class="glyphicon form-control-feedback"></span>
									  
									</div>
									<span style="display:none;" class="error-msg" for="url"><fmt:message key="register.page.form.url.validation" /></span>
								</div>
							</div>
							<div class="row">
								<div class="col-lg-9 col-sm-0"></div>
								<div class="col-lg-3 col-sm-12 col-xs-12">
									<button type="submit" class="btn blue col-lg-12 col-sm-12 col-xs-12"><fmt:message key="register.page.form.register.button.text" /></button>
								</div>
							</div>
			</div>
			
		</form>
	</div>
	<script>
	
	var urlTimeout;
	var urlRow = $('.error-after');
	
		$('[name=url]').keyup(function(){
			if(urlTimeout != undefined){
				clearTimeout(urlTimeout);
				
			}
			urlTimeout = setTimeout(function(){
				var ajaxUrl = location.href;
				$.post(ajaxUrl,{path : $('[name=url]').val()},function(available){
					
					if(available){
						urlRow.removeClass('has-error').addClass('has-success')
							.find('.form-control-feedback').removeClass('glyphicon-remove').addClass('glyphicon-ok');
						urlRow.find('.error-msg').hide();
					}else{
						urlRow.removeClass('has-success').addClass('has-error')
						.find('.form-control-feedback').removeClass('glyphicon-ok').addClass('glyphicon-remove');
						
						urlRow.find('.error-msg').show();
					}
				});
			},2000);
		});
	</script>
</body>
</html>