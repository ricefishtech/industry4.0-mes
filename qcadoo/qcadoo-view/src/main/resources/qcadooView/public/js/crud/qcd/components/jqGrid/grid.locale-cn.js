;(function($){
/**
 * jqGrid Polish Translation
 * Łukasz Schab lukasz@freetree.pl
 * http://FreeTree.pl
 *
 * Updated names, abbreviations, currency and date/time formats for Polish norms (also corresponding with CLDR v21.0.1 --> http://cldr.unicode.org/index) 
 * Tomasz Pęczek tpeczek@gmail.com
 * http://tpeczek.blogspot.com; http://tpeczek.codeplex.com
 *
 * Dual licensed under the MIT and GPL licenses:
 * http://www.opensource.org/licenses/mit-license.php
 * http://www.gnu.org/licenses/gpl.html
**/
$.jgrid = $.jgrid || {};
$.extend($.jgrid,{
	defaults : {
		recordtext: "显示 {0} - {1} of {2}",
		emptyrecords: "没有记录显示",
		loadtext: "正在加载...",
		savetext: "正在保存...",
		pgtext : "页面 {0} of {1}",
		pgfirst : "首页",
		pglast : "末页",
		pgnext : "下一页",
		pgprev : "上一页",
		pgrecs : "每页记录",
		showhide: "切换展开折叠网格"
	},
	search : {
		caption: "搜索...",
		Find: "查找",
		Reset: "重置",
		odata: [{ oper:'eq', text:'等于'},{ oper:'ne', text:'不等于'},{ oper:'lt', text:'小于'},{ oper:'le', text:'小于或等于'},{ oper:'gt', text:'大于'},{ oper:'ge', text:'大于或等于'},{ oper:'bw', text:'开始于'},{ oper:'bn', text:'不是以'},{ oper:'in', text:'包括'},{ oper:'ni', text:'不包括'},{ oper:'ew', text:'结束'},{ oper:'en', text:'不以结束'},{ oper:'cn', text:'包含'},{ oper:'nc', text:'不包含'},{ oper:'nu', text:'为空'},{ oper:'nn', text:'不为空'}],
		groupOps: [{ op: "AND", text: "所有" },{ op: "OR",  text: "任意" }],
		operandTitle : "单击以选中搜索操作.",
		resetTitle : "重置搜索条件"
	},
	edit : {
		addCaption: "增加记录",
		editCaption: "编辑记录",
		bSubmit: "提交",
		bCancel: "取消",
		bClose: "关闭",
		saveData: "数据已经改变！ 保存更改？",
		bYes : "是",
		bNo : "否",
		bExit : "取消",
		msg: {
			required:"字段是必需的",
			number:"请输入有效编号",
			minValue:"值必须大于或等于 ",
			maxValue:"值必须小于或等于",
			email: "不是有效的电子邮件",
			integer: "请输入有效的整数值",
			date: "请输入有效日期值",
			url: "不是有效的URL，需要前缀 ('http://' or 'https://')",
			nodefined : " 没有定义！",
			novalue : " 返回值是必需的!",
			customarray : "自定义函数应该返回数组!",
			customfcheck : "在自定义检查的情况下应该存在自定义功能!"
		}
	},
	view : {
		caption: "显示记录",
		bClose: "关闭"
	},
	del : {
		caption: "删除",
		msg: "删除选择的记录?",
		bSubmit: "删除",
		bCancel: "取消"
	},
	nav : {
		edittext: "",
		edittitle: "编辑选择的行",
		addtext:"",
		addtitle: "添加新行",
		deltext: "",
		deltitle: "删除选择的行",
		searchtext: "",
		searchtitle: "查找记录",
		refreshtext: "",
		refreshtitle: "重载表格",
		alertcap: "Warning",
		alerttext: "请选择行",
		viewtext: "",
		viewtitle: "查看选择的行",
		savetext: "",
		savetitle: "保存行",
		canceltext: "",
		canceltitle : "取消编辑行"
	},
	col : {
		caption: "选择列",
		bSubmit: "确定",
		bCancel: "取消"
	},
	errors : {
		errcap : "Error",
		nourl : "未设置url",
		norecords: "没有记录处理",
		model : "Length of colNames <> colModel!"
	},
	formatter : {
		integer : {thousandsSeparator: ",", defaultValue: '0'},
		number : {decimalSeparator:".", thousandsSeparator: ",", decimalPlaces: 2, defaultValue: '0.00'},
		currency : {decimalSeparator:".", thousandsSeparator: ",", decimalPlaces: 2, prefix: "", suffix:"", defaultValue: '0.00'},
		date : {
			dayNames:   [
				"周日", "周一", "周二", "周三", "周四", "周五", "周六",
				"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"
			],
			monthNames: [
				"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月",
				"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"
			],
			AmPm : ["am","pm","AM","PM"],
			S: function (j) {return j < 11 || j > 13 ? ['st', 'nd', 'rd', 'th'][Math.min((j - 1) % 10, 3)] : 'th';},
			srcformat: 'Y-m-d',
			newformat: 'Y/n/j',
			parseRe : /[#%\\\/:_;.,\t\s-]/,
			masks : {
				// see http://php.net/manual/en/function.date.php for PHP format used in jqGrid
				// and see http://docs.jquery.com/UI/Datepicker/formatDate
				// and https://github.com/jquery/globalize#dates for alternative formats used frequently
				// one can find on https://github.com/jquery/globalize/tree/master/lib/cultures many
				// information about date, time, numbers and currency formats used in different countries
				// one should just convert the information in PHP format
				ISO8601Long:"Y-m-d H:i:s",
				ISO8601Short:"Y-m-d",
				// short date:
				//    n - Numeric representation of a month, without leading zeros
				//    j - Day of the month without leading zeros
				//    Y - A full numeric representation of a year, 4 digits
				// example: 3/1/2012 which means 1 March 2012
				ShortDate: "Y/n/j", // in jQuery UI Datepicker: "M/d/yyyy"
				// long date:
				//    l - A full textual representation of the day of the week
				//    F - A full textual representation of a month
				//    d - Day of the month, 2 digits with leading zeros
				//    Y - A full numeric representation of a year, 4 digits
				LongDate: "Y F d,l", // in jQuery UI Datepicker: "dddd, MMMM dd, yyyy"
				// long date with long time:
				//    l - A full textual representation of the day of the week
				//    F - A full textual representation of a month
				//    d - Day of the month, 2 digits with leading zeros
				//    Y - A full numeric representation of a year, 4 digits
				//    g - 12-hour format of an hour without leading zeros
				//    i - Minutes with leading zeros
				//    s - Seconds, with leading zeros
				//    A - Uppercase Ante meridiem and Post meridiem (AM or PM)
				FullDateTime: "Y F d,l, g:i:s A", // in jQuery UI Datepicker: "dddd, MMMM dd, yyyy h:mm:ss tt"
				// month day:
				//    F - A full textual representation of a month
				//    d - Day of the month, 2 digits with leading zeros
				MonthDay: "F d", // in jQuery UI Datepicker: "MMMM dd"
				// short time (without seconds)
				//    g - 12-hour format of an hour without leading zeros
				//    i - Minutes with leading zeros
				//    A - Uppercase Ante meridiem and Post meridiem (AM or PM)
				ShortTime: "g:i A", // in jQuery UI Datepicker: "h:mm tt"
				// long time (with seconds)
				//    g - 12-hour format of an hour without leading zeros
				//    i - Minutes with leading zeros
				//    s - Seconds, with leading zeros
				//    A - Uppercase Ante meridiem and Post meridiem (AM or PM)
				LongTime: "g:i:s A", // in jQuery UI Datepicker: "h:mm:ss tt"
				SortableDateTime: "Y-m-d\\TH:i:s",
				UniversalSortableDateTime: "Y-m-d H:i:sO",
				// month with year
				//    Y - A full numeric representation of a year, 4 digits
				//    F - A full textual representation of a month
				YearMonth: "Y, F" // in jQuery UI Datepicker: "MMMM, yyyy"
			},
			reformatAfterEdit : false,
			userLocalTime : false
		},
		baseLinkUrl: '',
		showAction: '',
		target: '',
		checkbox : {disabled:true},
		idName : 'id'
	}
});
})(jQuery);