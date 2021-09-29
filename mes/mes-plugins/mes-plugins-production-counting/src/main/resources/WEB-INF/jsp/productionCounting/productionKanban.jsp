
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta charset="utf-8" />
    <title>云MES-智能制造解决方案</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <meta content="云MES Dashboard" name="description" />
    <meta content="云MES Dashboard" name="author" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />

    <link href="/productionCounting/public/css/jquery.circliful.css" rel="stylesheet" type="text/css" />

    <link href="/productionCounting/public/css/dataTables.bootstrap4.min.css" rel="stylesheet" type="text/css" />

    <!-- App css -->
    <link href="/productionCounting/public/css/bootstrap4.min.css" rel="stylesheet" type="text/css" />
    <link href="/productionCounting/public/css/icons.css" rel="stylesheet" type="text/css" />
    <link href="/productionCounting/public/css/style.css" rel="stylesheet" type="text/css" />

    <script src="/productionCounting/public/js/modernizr.min.js"></script>

</head>

<body>

<!-- Navigation Bar-->
<header id="topnav">

</header>
<!-- End Navigation Bar-->


<div class="wrapper">
    <div id="dataCount" class="container-fluid">


        <div class="row">
            <div class="col-sm-6 col-lg-3">
                <div class="widget-simple-chart text-center card-box">
                    <h3 class="text-success counter m-t-10">${totalPlannedProductionOrder.quantity}</h3>
                    <p class="text-muted text-nowrap m-b-10">订单总数</p>
                </div>
            </div>

            <div class="col-sm-6 col-lg-3">
                <div class="widget-simple-chart text-right card-box">
                    <div class="circliful-chart" data-dimension="90" data-text="${producedOrder.percentage}%" data-width="5" data-fontsize="14" data-percent="${producedOrder.percentage}" data-fgcolor="#3bafda" data-bgcolor="#505A66"></div>
                    <h3 class="text-primary counter m-t-10">${producedOrder.quantity}</h3>
                    <p class="text-muted text-nowrap m-b-10">已生产订单</p>
                </div>
            </div>

            <div class="col-sm-6 col-lg-3">
                <div class="widget-simple-chart text-right card-box">
                    <div class="circliful-chart" data-dimension="90" data-text="${shippedOrder.percentage}%" data-width="5" data-fontsize="14" data-percent="${shippedOrder.percentage}" data-fgcolor="#f76397" data-bgcolor="#505A66"></div>
                    <h3 class="text-pink counter m-t-10">${shippedOrder.quantity}</h3>
                    <p class="text-muted text-nowrap m-b-10">已发货订单</p>
                </div>
            </div>

            <div class="col-sm-6 col-lg-3">
                <div class="widget-simple-chart text-right card-box">
                    <div class="circliful-chart" data-dimension="90" data-text="${productionOrder.percentage}%" data-width="5" data-fontsize="14" data-percent="${productionOrder.percentage}" data-fgcolor="#98a6ad" data-bgcolor="#505A66"></div>
                    <h3 class="text-muted counter m-t-10">${productionOrder.quantity}</h3>
                    <p class="text-muted text-nowrap m-b-10">生产中订单</p>
                </div>
            </div>
        </div>
        <!-- end row -->


            <div class="row">
                <div class="col-12">
                    <div class="card-box table-responsive">
                        <h4 class="m-t-0 header-title">实时生产订单</h4>

                        <table id="datatable" class="table table-bordered">
                            <thead>
                            <tr>
                                <th>订单编号</th>
                                <th>产品编号</th>
                                <th>开始日期</th>
                                <th>结束日期</th>
                                <th>数量</th>
                            </tr>
                            </thead>


                            <tbody>

                            </tbody>
                        </table>
                    </div>
                </div>
            </div> <!-- end row -->




    </div> <!-- end container -->
</div>
<!-- end wrapper -->


<!-- Footer -->
<footer class="footer">
    <div class="container">
        <div class="row">
            <div class="col-12 text-center">
                2016 - 2020 © Ricefish Co.,Ltd. www.cloudmes.io Email: contact@cloudmes.io
            </div>
        </div>
    </div>
</footer>
<!-- End Footer -->


<!-- jQuery  -->
<script src="/productionCounting/public/js/jquery.min.js"></script>
<script src="/productionCounting/public/js/popper.min.js"></script><!-- Popper for Bootstrap --><!-- Tether for Bootstrap -->
<script src="/productionCounting/public/js/bootstrap4.min.js"></script>
<script src="/productionCounting/public/js/waves.js"></script>
<script src="/productionCounting/public/js/jquery.slimscroll.js"></script>
<script src="/productionCounting/public/js/jquery.scrollTo.min.js"></script>

<!-- Counter Up  -->
<script src="/productionCounting/public/js/jquery.waypoints.min.js"></script>
<script src="/productionCounting/public/js/jquery.counterup.min.js"></script>

<!-- circliful Chart -->
<script src="/productionCounting/public/js/jquery.circliful.min.js"></script>
<script src="/productionCounting/public/js/jquery.sparkline.min.js"></script>

<!-- skycons -->
<script src="/productionCounting/public/js/skycons.min.js" type="text/javascript"></script>

<script src="/productionCounting/public/js/jquery.dataTables.min.js"></script>
<script src="/productionCounting/public/js/dataTables.bootstrap4.min.js"></script>

<!-- Page js  -->
<script src="/productionCounting/public/js/jquery.dashboard.js"></script>

<!-- Custom main Js -->
<script src="/productionCounting/public/js/jquery.core.js"></script>
<script src="/productionCounting/public/js/jquery.app.js"></script>

<script type="text/javascript">
    jQuery(document).ready(function($) {
        $('.counter').counterUp({
            delay: 100,
            time: 1200
        });
        $('.circliful-chart').circliful();
    });


    // Default Datatable
   var dttable = $('#datatable').DataTable( {
        refresh: "/getProductionOrderList.html",
        language: {
            "lengthMenu" : '每页显示<select class="form-control input-xsmall">' + '<option value="5">5</option>'
                + '<option value="10">10</option>'
                + '<option value="20">20</option>'
                + '<option value="30">30</option>'
                + '<option value="50">50</option>' + '</select>条',
            "paginate" : {
                "first" : "首页",
                "last" : "末页",
                "previous" : "上一页",
                "next" : "下一页"
            },
            "processing" : "加载中...",  //DataTables载入数据时，是否显示‘进度’提示
            "emptyTable" : "暂无数据",
            "info" : "共 _PAGES_ 页  _TOTAL_ 条数据  ",
            "infoEmpty" : "暂无数据",
            "emptyTable" : "暂无数据...",  //表格中无数据
            "search": "搜索:",
            "infoFiltered" : " —— 从  _MAX_ 条数据中筛选",
            "zeroRecords":    "没有找到记录"

        },
        "processing": true,
        "serverSide": false,//前端处理分页
        "paging": true,//开启表格分页
        "pagingType": "full_numbers",
        columnDefs: [{
            "targets": 'nosort', //列的样式名
            "orderable": false //包含上样式名‘nosort'的禁止排序
        }],
        // "bAutoWidth" : false,//是否自动适应宽度
        "deferRender":true,//延迟渲染
        "renderer": "bootstrap",
        "ajax": {
            "url":  "/getProductionOrderList.html",
            "method": "GET",
        },

        // "deferLoading": 30,
        columns: [
            {
                "data": "ordernumber",
                "sWidth" : "25%"//列的宽度
            },
            {
                "data": "productnumber",
                "sWidth" : "25%"//列的宽度
            },
            {
                "data": function(obj){
                    return getMyDate(obj.timerangefrom)//通过调用函数来格式化所获取的时间戳
                },
                "sWidth" : "20%"//列的宽度
            },
            {
                "data": function(obj){
                    return getMyDate(obj.timerangeto)//通过调用函数来格式化所获取的时间戳
                },
                "sWidth" : "20%"//列的宽度
            },
            {
                "data": "usedquantity",
                "sWidth" : "10%"//列的宽度
            }
        ]

    } );


    // BEGIN SVG WEATHER ICON
    if (typeof Skycons !== 'undefined'){
        var icons = new Skycons(
            {"color": "#3bafda"},
            {"resizeClear": true}
            ),
            list  = [
                "clear-day", "clear-night", "partly-cloudy-day",
                "partly-cloudy-night", "cloudy", "rain", "sleet", "snow", "wind",
                "fog"
            ],
            i;

        for(i = list.length; i--; )
            icons.set(list[i], list[i]);
        icons.play();
    };


    function getMyDate(time){
        var oDate = new Date(time),
            oYear = oDate.getFullYear(),
            oMonth = oDate.getMonth()+1,
            oDay = oDate.getDate(),
            oHour = oDate.getHours(),
            oMin = oDate.getMinutes(),
            oSen = oDate.getSeconds(),
            oTime = oYear +'-'+ getzf(oMonth) +'-'+ getzf(oDay) +' '+ getzf(oHour) +':'+ getzf(oMin) +':'+getzf(oSen);//最后拼接时间
        return oTime;
    }

    //补0操作,当时间数据小于10的时候，给该数据前面加一个0
    function getzf(num){
        if(parseInt(num) < 10){
            num = '0'+num;
        }
        return num;
    }

</script>
<script type="text/javascript">
    $(function() {
        setInterval( function () {
            dttable.ajax.reload( null, false ); // user paging is not reset on reload
        }, 30000 );
    })

</script>
</body>
</html>