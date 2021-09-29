
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
            <div class="container-fluid">


                <div class="row">
                    <div class="col-sm-6 col-lg-3">
                        <div class="widget-simple-chart text-right card-box">
                            <div class="circliful-chart" data-dimension="90" data-text="35%" data-width="5" data-fontsize="14" data-percent="35" data-fgcolor="#5fbeaa" data-bgcolor="#505A66"></div>
                            <h3 class="text-success counter m-t-10">2562</h3>
                            <p class="text-muted text-nowrap m-b-10">订单总数</p>
                        </div>
                    </div>

                    <div class="col-sm-6 col-lg-3">
                        <div class="widget-simple-chart text-right card-box">
                            <div class="circliful-chart" data-dimension="90" data-text="75%" data-width="5" data-fontsize="14" data-percent="75" data-fgcolor="#3bafda" data-bgcolor="#505A66"></div>
                            <h3 class="text-primary counter m-t-10">5685</h3>
                            <p class="text-muted text-nowrap m-b-10">已生产订单</p>
                        </div>
                    </div>

                    <div class="col-sm-6 col-lg-3">
                        <div class="widget-simple-chart text-right card-box">
                            <div class="circliful-chart" data-dimension="90" data-text="58%" data-width="5" data-fontsize="14" data-percent="58" data-fgcolor="#f76397" data-bgcolor="#505A66"></div>
                            <h3 class="text-pink m-t-10">$ <span class="counter">12480</span></h3>
                            <p class="text-muted text-nowrap m-b-10">已发货订单</p>
                        </div>
                    </div>

                    <div class="col-sm-6 col-lg-3">
                        <div class="widget-simple-chart text-right card-box">
                            <div class="circliful-chart" data-dimension="90" data-text="49%" data-width="5" data-fontsize="14" data-percent="49" data-fgcolor="#98a6ad" data-bgcolor="#505A66"></div>
                            <h3 class="text-muted counter m-t-10">62</h3>
                            <p class="text-muted text-nowrap m-b-10">生产中订单</p>
                        </div>
                    </div>
                </div>
                <!-- end row -->




                <div class="row">
                    <div class="col-lg-4">
                        <div class="card-box">
                            <h4 class="header-title m-t-0 m-b-30">销售汇总</h4>

                            <div class="widget-chart text-center">
                                <div id="sparkline1"></div>
                                <ul class="list-inline m-t-15 mb-0">
                                    <li>
                                        <h5 class="text-muted m-t-20">总销售额</h5>
                                        <h4 class="m-b-0">$56,214</h4>
                                    </li>
                                    <li>
                                        <h5 class="text-muted m-t-20">上周</h5>
                                        <h4 class="m-b-0">$98,251</h4>
                                    </li>
                                    <li>
                                        <h5 class="text-muted m-t-20">上月</h5>
                                        <h4 class="m-b-0">$10,025</h4>
                                    </li>
                                </ul>
                            </div>
                        </div>

                    </div>

                    <div class="col-lg-4">
                        <div class="card-box">
                            <h4 class="header-title m-t-0 m-b-30">生产汇总</h4>

                            <div class="widget-chart text-center">
                                <div id="sparkline2"></div>
                                <ul class="list-inline m-t-15 mb-0">
                                    <li>
                                        <h5 class="text-muted m-t-20">生产订单总额</h5>
                                        <h4 class="m-b-0">$1000</h4>
                                    </li>
                                    <li>
                                        <h5 class="text-muted m-t-20">上周</h5>
                                        <h4 class="m-b-0">$523</h4>
                                    </li>
                                    <li>
                                        <h5 class="text-muted m-t-20">上月</h5>
                                        <h4 class="m-b-0">$965</h4>
                                    </li>
                                </ul>
                            </div>
                        </div>

                    </div>

                    <div class="col-lg-4">
                        <div class="card-box">
                            <h4 class="header-title m-t-0 m-b-30">产线汇总</h4>

                            <div class="widget-chart text-center">
                                <div id="sparkline3"></div>
                                <ul class="list-inline m-t-15 mb-0">
                                    <li>
                                        <h5 class="text-muted m-t-20">产线利用汇总</h5>
                                        <h4 class="m-b-0">$1,84,125</h4>
                                    </li>
                                    <li>
                                        <h5 class="text-muted m-t-20">上周</h5>
                                        <h4 class="m-b-0">$50,230</h4>
                                    </li>
                                    <li>
                                        <h5 class="text-muted m-t-20">上月</h5>
                                        <h4 class="m-b-0">$87,451</h4>
                                    </li>
                                </ul>
                            </div>
                        </div>

                    </div>

                </div>
                <!-- end row -->


                <div class="row">
                    <div class="col-lg-8">
                        <div class="card-box">
                            <h4 class="header-title m-t-0">最新生产订单</h4>
                            <p class="text-muted m-b-25 font-13">
                                只显示最新5条
                            </p>

                            <div class="table-responsive">
                                <table class="table mb-0">
                                    <thead>
                                    <tr>
                                        <th>#</th>
                                        <th>订单编号</th>
                                        <th>开始日期</th>
                                        <th>结束日期</th>
                                        <th>状态</th>
                                        <th>生产线</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <tr>
                                        <td>1</td>
                                        <td>Minton Admin v1</td>
                                        <td>01/01/2017</td>
                                        <td>26/04/2017</td>
                                        <td><span class="badge badge-info">Released</span></td>
                                        <td>Coderthemes</td>
                                    </tr>
                                    <tr>
                                        <td>2</td>
                                        <td>Minton Frontend v1</td>
                                        <td>01/01/2017</td>
                                        <td>26/04/2017</td>
                                        <td><span class="badge badge-success">Released</span></td>
                                        <td>Minton admin</td>
                                    </tr>
                                    <tr>
                                        <td>3</td>
                                        <td>Minton Admin v1.1</td>
                                        <td>01/05/2017</td>
                                        <td>10/05/2017</td>
                                        <td><span class="badge badge-pink">Pending</span></td>
                                        <td>Coderthemes</td>
                                    </tr>
                                    <tr>
                                        <td>4</td>
                                        <td>Minton Frontend v1.1</td>
                                        <td>01/01/2017</td>
                                        <td>31/05/2017</td>
                                        <td><span class="badge badge-purple">Work in Progress</span>
                                        </td>
                                        <td>Minton admin</td>
                                    </tr>
                                    <tr>
                                        <td>5</td>
                                        <td>Minton Admin v1.3</td>
                                        <td>01/01/2017</td>
                                        <td>31/05/2017</td>
                                        <td><span class="badge badge-warning">Coming soon</span></td>
                                        <td>Coderthemes</td>
                                    </tr>

                                    </tbody>
                                </table>
                            </div>

                        </div>
                    </div>
                    <!-- end col -8 -->

                    <div class="col-lg-4">
                        <div class="card-box widget-user">
                            <div>
                                <div class="wid-u-info">
                                    <h5 class="mt-0 m-b-5 font-16">100</h5>
                                    <p class="text-muted m-b-5 font-13">排班人数</p>
                                    <small class="text-warning"><b>白班</b></small>
                                </div>
                            </div>
                        </div>

                        <div class="card-box widget-user">
                            <div>
                                <div class="wid-u-info">
                                    <h5 class="mt-0 m-b-5 font-16">100</h5>
                                    <p class="text-muted m-b-5 font-13">排班人数</p>
                                    <small class="text-success"><b>晚班</b></small>
                                </div>
                            </div>
                        </div>

                        <div class="card-box widget-user">
                            <div>
                                <div class="wid-u-info">
                                    <h5 class="mt-0 m-b-5 font-16">100</h5>
                                    <p class="text-muted m-b-5 font-13">排班人数</p>
                                    <small class="text-pink"><b>中班</b></small>
                                </div>
                            </div>
                        </div>

                    </div>

                </div>
                <!-- end row -->


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

        </script>

    </body>
</html>