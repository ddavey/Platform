$(document).ready(function(){
	
	jQuery.fn.extend({validators : ['minlength','mandatory','pwdstrength','match'],
		validate : function(){
		var valid = true;
		if($(this).data('minlength')){
			var minLen = $(this).data('minlength');
			if($(this).val().length >= minLen){
				valid = true;
			}else {
				valid = false;
				if($(this).data('minlength-msg')){
					$(this).parent().find('.error-msg').text($(this).data('minlength-msg'));
				}
			}
		}else if($(this).data('mandatory')!=undefined){
			if($(this).attr('type')=="email"){
				var emailRegEx = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
				valid = emailRegEx.test($(this).val());
				$(this).parent().find('.error-msg').text($(this).data('mandatory-msg'));
			}
			
		}else if($(this).data('pwdstrength')>0){
			if($(this).attr('type')=="password"){
				var value = $(this).val(),char = null, upper = false, lower = false, number = false;length = false,
				
				valid = value.match(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$/g) != null;
				
				if(valid){
					$(this).parent().find('.error-msg').text($(this).data('pwdstrength-msg'));
				}
				
			}
		}else if($(this).data('match')){
			var matchElem = $('[name='+$(this).data('match')+']');
			if($(this).val() == matchElem.val()){
				valid = true;
			}else {
				valid = false;
				if($(this).data('match-msg')){
					$(this).parent().find('.error-msg').text($(this).data('match-msg'));
				}
			}
		}
		
		
		
		return valid;
	},requiresValidation : function(){
		
		var field = this;
		
		var hasValidator = false;
		
		$.each(this.validators,function(){
			if(field.data(this.toString())!=undefined){
				hasValidator = true;
				return true;
			}
		});
		return hasValidator;
	}});
	
	
	var fields = $('input,select,textarea');
	
	$.each(fields,function(){
		var field = $(this);
		$.each($(this).validators,function(){
			if(field.attr(this.toString())!=undefined ){
				if(field.validators[0] == this.toString() || field.validators[2] == this.toString()){
					$(field).data(this.toString(),parseInt($(field).attr(this.toString()))).removeAttr(this.toString());
				}else{
					$(field).data(this.toString(),$(field).attr(this.toString())).removeAttr(this.toString());
				}
				
				
				if(field.attr(this.toString()+"-msg")){
					if(field.validators[2] == this.toString()){
						
					}
					$(field).data(this.toString()+"-msg",$(field).attr(this.toString()+"-msg")).removeAttr(this.toString()+"-msg");
				};
			}
		});
		
	});
	
	fields.focusout(function(evt){
		if($(evt.target).requiresValidation()){
						
			var inputGroup = $(evt.target).parent();
			while(!inputGroup.hasClass('input-group')){
				inputGroup = $(inputGroup).parent();
			}
			
			if(inputGroup.find('.form-control-feedback').length == 0){
				inputGroup.append('<span class="glyphicon form-control-feedback"></span><span class="error-msg" for="'+$(evt.target).prop('name')+'"></span>');
			}
			
			var valid = $(evt.target).validate();
			
			if(valid){
				inputGroup.addClass('has-success').removeClass('has-error');
				inputGroup.find('.form-control-feedback').removeClass('glyphicon-remove').addClass('glyphicon-ok');
				inputGroup.find('.error-msg').hide();
			}else{
				inputGroup.addClass('has-error').removeClass('has-success');
				inputGroup.find('.form-control-feedback').removeClass('glyphicon-ok').addClass('glyphicon-remove');
				inputGroup.find('.error-msg').show();
			}
		}
		
	});
	
	
	
});

function displayFieldErrors(errorMap){
	
	var field;
	$.each(errorMap,function(key){
		
		field = $('[name='+key+']');
		
		field.parent().addClass('has-error');
		
		if(field.parent().find('.form-control-feedback').length == 0){
			field.parent().append('<span class="glyphicon form-control-feedback"></span><span class="error-msg" for="'+$(field).prop('name')+'"></span>');
		}
		
		field.parent().find('.form-control-feedback').addClass('glyphicon-remove');
		
		
		$('.error-msg[for='+key+']').text(this.toString()).show();
	});
	
}

function displayCorrectFieldValues(valid){

	$.each(valid,function(){
		field = $('[name='+this.toString()+']');
		
		field.parent().addClass('has-success');
		
		if(field.parent().find('.form-control-feedback').length == 0){
			field.parent().append('<span class="glyphicon form-control-feedback"></span>');
		}
		
		field.parent().find('.form-control-feedback').addClass('glyphicon-ok');
		
	});
	
}