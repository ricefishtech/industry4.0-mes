
$(function() {
	$('#fileupload')
			.fileupload(
					{
						
						dataType : 'json',
						acceptFileTypes : /(\.|\/)(gif|jpe?g|png|pdf|xls|xlsx|dwg|ipt|iam|idw|docx?|txt|csv|xml|odt|ods|tiff?)$/i,

						submit : function(e, data) {
							var locale = window.mainController
							.getComponentByReferenceName(
								"deliveryMultiUploadLocale")
									.getValue().content.value;
							var techId = window.mainController
								.getComponentByReferenceName(
									"deliveryIdForMultiUpload")
										.getValue();
							var techIdValue = techId.content;
							if(!techIdValue.value || 0 === techIdValue.value){
							    $.each(data.files, function (index, file) {
									if(locale === "pl_PL" || locale === "pl"){
							    	showMessage("failure",
											"Dostawa niezapisana",
											"Pominięto wgranie pliku: "
													+ file.name);
									} else {
								    	showMessage("failure",
												"Delivery is not saved",
												"Omitted file upload: "
														+ file.name);
									}
									});

								return false;
							}
						},
						done : function(e, data) {
							var gr = window.mainController
									.getComponentByReferenceName("form");
							gr.performRefresh;
							mainController.performRefresh;
							var mainViewComponent = mainController
									.getComponentByReferenceName("form")
									|| mainController
											.getComponentByReferenceName("grid");
							if (mainViewComponent) {
								mainViewComponent.performRefresh();
							}

							var locale = window.mainController
							.getComponentByReferenceName(
								"deliveryMultiUploadLocale")
									.getValue().content.value;
							var filetype = /(\.|\/)(gif|jpe?g|png|pdf|xls|xlsx|dwg|ipt|iam|idw|docx?|txt|csv|xml|odt|ods|tiff?)$/i;

							$.each(data.files, function(index, file) {
								if (filetype.test(file.name)) {
								if(locale === "pl_PL" || locale === "pl"){
									showMessage("success", "Wgrywanie zakończone",
											"Wgrano plik: " + file.name);
								} else {
									showMessage("success", "Uploading completed",
											"Loaded a file: " + file.name);
								}
								}
							});
						},

						progressall : function(e, data) {
							var progress = parseInt(data.loaded / data.total
									* 100, 10);
							$('#progress .progress-bar').css('width',
									progress + '%');
						},
						dropZone : $('#dropzone')
					}).bind(
					'fileuploadsubmit',
					function(e, data) {
						var techId = window.mainController
								.getComponentByReferenceName(
										"deliveryIdForMultiUpload")
								.getValue();
						var techIdValue = techId.content;
						data.formData = {
							techId : techIdValue.value
						};
					
					}).bind(
					'fileuploadadd',
					function(e, data) {
						var filetype = /(\.|\/)(gif|jpe?g|png|pdf|xls|xlsx|dwg|ipt|iam|idw|docx?|txt|csv|xml|odt|ods|tiff?)$/i;
						var locale = window.mainController
						.getComponentByReferenceName(
							"deliveryMultiUploadLocale")
								.getValue().content.value;
						$.each(data.files, function(index, file) {
							if (!filetype.test(file.name)) {
								if(locale === "pl_PL" || locale === "pl"){
								showMessage("failure",
										"Pominięto wgranie pliku",
										"Niedopuszczalny typ pliku: "
												+ file.name);
								} else {
									showMessage("failure",
											"Omitted file upload",
											"Invalid file type: "
													+ file.name);
								}
								return false;
							}

						});
					});

});

function showMessage(type, title, content) {
	mainController.showMessage({
		type : type,
		title : title,
		content : content
	});
}

