$(document).ready(function() {
    $("#divContent").html(imgPath);
    Android.getPassSetup();

    setTimeout(function (){
      // Something you want delayed.
        Android.downloadDevice();
        setInterval(reDownload, 5000);
    }, 1000);

    $("#btnCheck").prop("readonly",true);

    $("#chkShowHide").click(function(){
        if( $(this).is(':checked') )
        {
           //console.log("chkShowHide_click: true");
           $('#passSetup').prop('type', 'text');
        }
        else
        {
           //console.log("chkShowHide_click: false");
           $('#passSetup').prop('type', 'password');
        }
    });

    $("#chkSavePass").click( function(){
        console.log("chkSavePass_click");
        var cmd = "";
        if( $(this).is(':checked') )
          cmd = "YES";
        else
          cmd = "NO";
        Android.save_unsave(cmd);
    });
});

function reDownload()
{
    var x = $("#divContent").has("img").length;
    if(x == 1)
    {
        Android.downloadDevice();
    }
};

//Mở cài đặt
function showPassSetup(pass, isSave)
{
    $('#passSetup').val(pass);
    if(isSave == "YES")
    {
        $("#chkSavePass").prop('checked', true);
    }
    else if(isSave == "NO")
    {
        $("#chkSavePass").prop('checked', false);
    }
};

//Mở cài đặt
function openSetting()
{
    $('#modalSetting').modal('show');
};

function checkPassSetup()
{
    var pass = $("#passSetup").val();
    if(pass.trim().length == 0)
    {
        Android.showToast("Mật khẩu không được để trống!");
        return;
    }
//    if(pass.trim().length > 10)
//    {
//        Android.showToast("Mật khẩu chỉ bao gồm 6 kí tự!");
//        return;
//    }
    Android.checkPassSetup(pass);
};

function validatePass()
{
    var pass = $("#passSetup").val();
     // If x is Not a Number or less than one or greater than 10
    if (isNaN(pass) || pass.length > 10) {
        $("#passalert").val("Mật khẩu chỉ gồm 6 kí tự số.");
        $("#btnCheck").prop("readonly",true);
    } else {
        $("#passalert").val()
        $("#btnCheck").prop("readonly",false);
    }
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
            var type = lsDevice[i].deviceType;

            if(type == "11")       // A1
            {
                s += "<div class='table-responsive' style='margin-top:10px;'>"+
                    "<span style='font-size: 30sp;font-weight:bold;color: white;'>"+ name + "</span> <br/>"+
                    "<table class='table groupone' style='border: none;'>"+
                    "<tr>"+
                    "<td><button data-id='1' data-type='11' data-address='"+address+"' onclick='sendControl(this)' data-name='on' type='button' class='btnStyle w3-large onoff22'> On </button></td> "+
                    "<td><button data-id='3' data-type='11' data-address='"+address+"' onclick='sendControl(this)' data-name='dec' type='button' class='btnStyle w3-large indec'> -- </button></td> "+
                    "<td><button data-id='5' data-type='11' data-address='"+address+"' onclick='sendControl(this)' data-name='tmr' type='button' class='btnStyle w3-large timer'> Timer </button></td> "+
                    "<td><button data-id='7' data-type='11' data-address='"+address+"' onclick='sendControl(this)' data-name='tem' type='button' class='btnStyle w3-large temmode'> Temp </button></td>"+
                    "</tr>"+
                    "<tr>"+
                    "<td><button data-id='2' data-type='11' data-address='"+address+"' onclick='sendControl(this)' data-name='off' type='button' class='btnStyle w3-large onoff22'> Off </button></td>"+
                    "<td><button data-id='4' data-type='11' data-address='"+address+"' onclick='sendControl(this)' data-name='inc' type='button' class='btnStyle w3-large indec'> ++ </button> </td>"+
                    "<td><button data-id='6' data-type='11' data-address='"+address+"' onclick='sendControl(this)' data-name='tof' type='button' class='btnStyle w3-large timer'> Toff </button> </td>"+
                    "<td><button data-id='8' data-type='11' data-address='"+address+"' onclick='sendControl(this)' data-name='mod' type='button' class='btnStyle w3-large temmode'> Mode </button> </td>"+
                    "</tr>"+
                    "</table>"+
                    "</div>";
            }
            else if(type == "23")   // B3
            {
                s += "<div class='table-responsive' style='margin-top:10px;'>"+
                    "<span style='font-size: 30sp;font-weight:bold;color: white;'>"+ name + " </span> <br/>"+
                    "<table class='table grouptwo' style='border: none;'>"+
                    "<tr>"+
                    "<td><button data-id='1'  data-type='23' data-address='"+address+"' onclick='sendControl(this)' data-name='on1' type='button' class='btnStyle w3-large onoff1'> On 1 </button></td> "+
                    "<td><button data-id='3' data-type='23' data-address='"+address+"' onclick='sendControl(this)' data-name='on2' type='button' class='btnStyle w3-large onoff2'> On 2 </button></td> "+
                    "<td><button data-id='5' data-type='23' data-address='"+address+"' onclick='sendControl(this)' data-name='on3' type='button' class='btnStyle w3-large onoff3'> On 3 </button> </td>"+
                    "<td><button data-id='7' data-type='23' data-address='"+address+"' onclick='sendControl(this)' data-name='onall' type='button' class='btnStyle w3-large onoffall'> On All </button></td>"+
                    "</tr>"+
                    "<tr>"+                    
                    "<td><button data-id='2' data-type='23' data-address='"+address+"' onclick='sendControl(this)' data-name='off1' type='button' class='btnStyle w3-large onoff1'> Off 1 </button></td> "+
                    "<td><button data-id='4' data-type='23' data-address='"+address+"' onclick='sendControl(this)' data-name='off2' type='button' class='btnStyle w3-large onoff2'> Off 2 </button> </td>"+
                    "<td><button data-id='6' data-type='23' data-address='"+address+"' onclick='sendControl(this)' data-name='off3' type='button' class='btnStyle w3-large onoff3'> Off 3 </button></td> "+
                    "<td><button data-id='8' data-type='23' data-address='"+address+"' onclick='sendControl(this)' data-name='offall' type='button' class='btnStyle w3-large onoffall'> Off All </button></td> "+
                    "</tr>"+
                    "</table>"+
                    "</div>";
            }
        }
    }
    else
    {
        s = "<div class='rows text-center' style='margin-top:10px;'><h3 style='color:red; font-weight:bold;'>No Device Found!</h3></div>";
//        Android.sendData("UPDATE");
    }
    console.log(s);
    document.getElementById('divContent').innerHTML  = s;

    for(var i = 0; i< lsDevice.length; i++)
    {
      var id = '#status_'+(lsDevice[i].deviceId);
      $(id).bootstrapToggle();
    }
};

function sendControl(btnControl)
{
   var id = btnControl.getAttribute('data-id');
   var address = btnControl.getAttribute('data-address');
   var type = btnControl.getAttribute('data-type');
   console.log("sendControl: "+ id +"; address: "+ address);
   Android.sendControl(id, address, type);
};

function noDevice()
{
    document.getElementById('divContent').innerHTML  = "<h3 style='color:red;'>Chưa có thiết bị.</h3>";
};

function logout(){
       Android.logout();
};

function openLogoutModal() {
    $("#modalLogout").modal('show');
};