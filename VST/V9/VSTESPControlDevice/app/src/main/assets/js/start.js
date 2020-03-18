$(document).ready(function() {
    console.log("This is start.js");
    Android.getIpPort();
});

function showIpPort(name, ip, port)
{
    $('#name').val(name);
    $("#ip").val(ip);
    $("#port").val(port);
};

function ConnectESP()
{
    try{
        var ip = $("#ip").val();
        var port = $("#port").val();
        Android.ConnectESP( ip, port);
    }
    catch(ex)
    {
        console.log(ex);
    }
};
function gotoLogin()
{
    var name = $('#name').val();
    var ip = $("#ip").val();
    var port = $("#port").val();
    console.log("ip= "+ip+ "; port: "+ port);
    Android.saveIpPort(name, ip, port);
    window.location.href = "file:///android_asset/www/login.html";
};

function getListIp()
{
    Android.getListIp();
};

function showListIp(lsMaster)
{
    if(lsMaster == null || lsMaster.length == 0)
    {
        $("#divIp").html("Chưa có địa chỉ nào được lưu.");
    }
    else
    {
        var s = "";
           s += "<table class='table table-hover table-border text-center'>";
           s += "<tr>";
           s += "<thead><td>Master</td><td>IP</td><td>Port</td></thead>";
           s += "</tr>";
           for(var i = 0; i< lsMaster.length; i++)
           {
              s += "<tr onclick='showCurrentIp(this)'><td>"+lsMaster[i].MasterName +"</td><td>"+lsMaster[i].MasterIp +"</td><td>"+lsMaster[i].MasterPort +"</td></tr>";
           }
           s += "</table>";
           //document.getElementById('divIp').innerHTML  = s;
           $("#divIp").html(s);
    }
    $('#modalListIp').modal('show');
};

function showCurrentIp(row)
{
     var name = row.cells[0].innerHTML;
     var ip = row.cells[1].innerHTML;
     var port = row.cells[2].innerHTML;

     $('#name').val(name);
     $('#ip').val(ip);
     $('#port').val(port);
     $('#modalListIp').modal('hide');
};

function clearListIp()
{
    Android.clearListIp();
}