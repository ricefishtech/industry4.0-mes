jQuery.fn.liveUpdate = function(list) {
	list = jQuery(list);

	if (list.length) {
		var rows = list.children('li').clone(), cache = rows.map(function() {
			return $(this).text();
		}), $searchResult = $('#searchResult'), $emptySearchResult = $('#emptySearchResult'), $tooManySearchResult = $('#tooManySearchResult'), $headerSearchForm = $('.headerSearchForm'), $headerMenuContent = $('.headerMenuContent');

		this.keyup(filter).keyup().keydown(move).keydown().parents('form')
				.submit(function() {
					return false;
				});
	}

	return this;

	function move(event) {
		var keycode = (event.keyCode ? event.keyCode : event.which);
		if ($.trim($(event.currentTarget).val()).length > 0) {
			if (keycode == '40') {
				var index = $('a', $searchResult).index(
						$('a.active', $searchResult));
				if (index == $('a', $searchResult).length - 1) {
					index = -1;
				}
				$('a.active', $searchResult).removeClass('active');
				$('a:eq(' + (index + 1) + ')', $searchResult)
						.addClass('active');
				return false;

			} else if (keycode == '38') {
				var index = $('a', $searchResult).index(
						$('a.active', $searchResult));
				if (index <= 0) {
					index = $('a', $searchResult).length;
				}
				$('a.active', $searchResult).removeClass('active');
				$('a:eq(' + (index - 1) + ')', $searchResult)
						.addClass('active');
				return false;
			}
			if (keycode == '13') {
				if ($('a.active', $searchResult).length > 0) {
					//					var href = $('a.active', $searchResult).attr('href');
					//					openPage(href);

					var href = $('a.active', $searchResult).parent().attr('id');
					var itemParts = href.split("_");
					$('.userMenuBackdoor').click();
					windowController.goToMenuPosition(itemParts[1] + "."
							+ itemParts[2]);
				}
			}
			if ($('.logoDropdownBox').hasClass('open')) {
				if (keycode == 27) {
					$('.userMenuBackdoor').click();
				}
			}
		}
	}

	function filter(event) {
		var keycode = (event.keyCode ? event.keyCode : event.which);

		var results = $("#searchResult .subMenu");
		if (keycode != '40' && keycode != '38') {
			var term = jQuery.trim(jQuery(this).val()), scores = [];

			$emptySearchResult.hide();
			$tooManySearchResult.hide();
			$headerSearchForm.removeClass('showClear');
			$headerMenuContent.removeClass('openSearchResult');
			$(".active", $searchResult).removeClass('active');
			$searchResult.removeHighlight();
			$("li", $searchResult).remove();
			if (term) {
				$headerSearchForm.addClass('showClear');
				$headerMenuContent.addClass('openSearchResult');

				cache.each(function(i) {
					var score = this.search(new RegExp(term, "i"));
					if (score > -1) {
						scores.push([score, i]);
					}
				});

				jQuery.each(scores, function() {
					$(".subMenu", $searchResult).append(rows[this[1]]);
				});

				if (scores.length < 1) {
					$emptySearchResult.show();
					$tooManySearchResult.hide();
					results.hide();
				} else if (scores.length > 12) {
					$emptySearchResult.hide();
					$tooManySearchResult.show();
					results.hide();
				} else {
					results.show();
					$("li", $searchResult).click(
							function(e) {
								var href = $(this).attr('id');
								var itemParts = href.split("_");
								windowController.goToMenuPosition(itemParts[1]
										+ "." + itemParts[2]);
								//openPage(href);
								$('.userMenuBackdoor').click();
								e.preventDefault();
							});
				}

				$searchResult.highlight(term);
			}
		}
	}
};