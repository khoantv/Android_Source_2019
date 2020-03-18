$(document).ready(function() {
    Android.getDevicesControl();
});

//Mở cài đặt
function openSetting()
{
    $('#modalSetting').modal('show');
};

function checkPassSetup()
{
    //window.location.href = "file:///android_asset/www/setting.html";
    pass = $("#passSetup").val();
    Android.checkPassSetup(pass);
};

//Tạo các nút điều khiển khi nhấn nút Control
function initControl(lsDevice)
{
    var s = "";
    if (lsDevice != null && lsDevice.length > 0)
    {
        for(var i = 0; i< lsDevice.length; i++)
        {
            var id = lsDevice[i].deviceId;
            var name = lsDevice[i].deviceName;
            var address = lsDevice[i].deviceAddress;

            s += "<div class='rows text-center' style='margin-top:10px;'>"+
            "<span style='font-size: 25sp;'>Thiết Bị: "+ name +" - "+ address +" </span> <br/>"+
            //"<span onclick='sendControl(this)' data-id='"+id+"' data-name='status'><input id='status_"+id+"' data-id='"+id+"' name='status' type='checkbox' checked data-onstyle='info'/></span> "+
            "<div><button data-id='?"+id+"=CMD1' onclick='sendControl(this)' data-name='on' type='button' class='btn btn-warning'><span class='glyphicon glyphicon-ok'></span></button> "+
            "<button data-id='?DV"+id+"=CMD2' onclick='sendControl(this)' data-name='off' type='button' class='btn btn-warning'><span class='glyphicon glyphicon-off'></span></button> "+
            "<button data-id='?DV"+id+"=CMD3' onclick='sendControl(this)' data-name='dec' type='button' class='btn btn-warning'><span class='glyphicon glyphicon-minus'></span></button> "+
            "<button data-id='?DV"+id+"=CMD4' onclick='sendControl(this)' data-name='inc' type='button' class='btn btn-warning'><span class='glyphicon glyphicon-plus'></span></button> "+
            "<button data-id='?DV"+id+"=CMD5' onclick='sendControl(this)' data-name='tem' type='button' class='btn btn-success'><span class='glyphicon glyphicon-fire'></span></button> "+
            "<button data-id='?DV"+id+"=CMD6' onclick='sendControl(this)' data-name='tmr' type='button' class='btn btn-danger'><span class='glyphicon glyphicon-time'></span></button> "+
            "<button data-id='?DV"+id+"=CMD7' onclick='sendControl(this)' data-name='tof' type='button' class='btn btn-danger'><span class='glyphicon glyphicon-calendar'></span></button> "+
            "<button data-id='?DV"+id+"=CMD8' onclick='sendControl(this)' data-name='mod' type='button' class='btn btn-success'><span class='glyphicon glyphicon-cog'></span></button> "+
            "</div><hr/>";
        }
    }
    else
    {
        s = "<div class='rows text-center' style='margin-top:10px;'><h3 style='color:red; font-weight:bold;'>No Device Found!</h3></div>";
//        Android.sendData("UPDATE");
    }
    //console.log(s);
    document.getElementById('divContent').innerHTML  = s;

    for(var i = 0; i< lsDevice.length; i++)
    {
      var id = '#status_'+(lsDevice[i].deviceId);
      $(id).bootstrapToggle();
    }
};

function sendControl(btnControl)
{
   var name = btnControl.getAttribute('data-name');
   var id = btnControl.getAttribute('data-id');
   console.log("sendControl: "+ id);
   //Android.sendData(id);
};