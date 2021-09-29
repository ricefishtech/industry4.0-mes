
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
    <div id="dataCount" class="container-fluid">


        <div class="row">
            <div class="col-lg-3 col-md-6">
                <div class="widget-simple text-center card-box">
                    <h3 class="text-dark counter font-bold mt-0">${totalProduct}</h3>
                    <p class="text-muted mb-0">产品总数</p>
                </div>
            </div>

            <div class="col-lg-3 col-md-6">
                <div class="widget-simple text-center card-box">
                    <h3 class="text-dark counter font-bold mt-0">${totalTechnology}</h3>
                    <p class="text-muted mb-0">工艺总数</p>
                </div>
            </div>

            <div class="col-lg-3 col-md-6">
                <div class="widget-simple text-center card-box">
                    <h3 class="text-dark counter font-bold mt-0">${totalStaff}</h3>
                    <p class="text-muted mb-0">工人总数</p>
                </div>
            </div>

            <div class="col-lg-3 col-md-6">
                <div class="widget-simple text-center card-box">
                    <h3 class="text-dark counter font-bold mt-0">${totalProductionLine}</h3>
                    <p class="text-muted mb-0">工厂/产线总数</p>
                </div>
            </div>
        </div>
        <!-- end row -->


        <div class="row">
            <div class="col-lg-3 col-md-6">
                <div class="widget-simple text-center card-box">
                    <h3 class="text-dark counter font-bold mt-0">${totalWarehouse}</h3>
                    <p class="text-muted mb-0">仓库总数</p>
                </div>
            </div>

            <div class="col-lg-3 col-md-6">
                <div class="widget-simple text-center card-box">
                    <h3 class="text-dark counter font-bold mt-0">${totalFinalProduct}</h3>
                    <p class="text-muted mb-0">成品总数</p>
                </div>
            </div>

            <div class="col-lg-3 col-md-6">
                <div class="widget-simple text-center card-box">
                    <h3 class="text-dark counter font-bold mt-0">${totalMaterial}</h3>
                    <p class="text-muted mb-0">原材料库存</p>
                </div>
            </div>

            <div class="col-lg-3 col-md-6">
                <div class="widget-simple text-center card-box">
                    <h3 class="text-dark counter font-bold mt-0">${totalMiddleware}</h3>
                    <p class="text-muted mb-0">半成品库存</p>
                </div>
            </div>
        </div>
        <!-- end row -->


        <div class="row">
            <div class="col-lg-3 col-md-6">
                <div class="widget-simple text-center card-box">
                    <h3 class="text-dark counter font-bold mt-0">${totalMasterOrder}</h3>
                    <p class="text-muted mb-0">主订单总数</p>
                </div>
            </div>

            <div class="col-lg-3 col-md-6">
                <div class="widget-simple text-center card-box">
                    <h3 class="text-dark counter font-bold mt-0">${totalProductionOrder}</h3>
                    <p class="text-muted mb-0">生产订单总数</p>
                </div>
            </div>

            <div class="col-lg-3 col-md-6">
                <div class="widget-simple text-center card-box">
                    <h3 class="text-dark counter font-bold mt-0">${totalMachine}</h3>
                    <p class="text-muted mb-0">机台总数</p>
                </div>
            </div>

            <div class="col-lg-3 col-md-6">
                <div class="widget-simple text-center card-box">
                    <h3 class="text-dark counter font-bold mt-0">${totalRepairTime}</h3>
                    <p class="text-muted mb-0">维修次数</p>
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

<script type="text/javascript">
    $(function() {
        setInterval(function() {
            $("#dataCount").ajax.reload( null, false );
        }, 30000);
    })

</script>

</body>
</html>