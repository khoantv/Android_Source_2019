$(document).ready(function() {
    Android.showMyIp();
    // Gọi hàm bên Java, đọc danh sách các thiết bị
    Android.getDevicesList();
});

function home()
{
  document.getElementById("home").style.display = "block";
  document.getElementById("control").style.display = "none";
  document.getElementById("setting").style.display = "none";
  document.getElementById("about").style.display = "none";
};

function control()
{
  document.getElementById("home").style.display = "none";
  document.getElementById("setting").style.display = "none";
  document.getElementById("about").style.display = "none";
  document.getElementById("control").style.display = "block";

  //Android.getDevicesList();
}

function setting()
{
  document.getElementById("home").style.display = "none";
  document.getElementById("control").style.display = "none";
  document.getElementById("about").style.display = "none";
  document.getElementById("setting").style.display = "block";
  // Gọi hàm bên Java, trả về danh mục thiết bị dạng json
  //  var s = '[{"Id":1, "DeviceName":"Đèn 1"},{"Id":2, "DeviceName":"Đèn 2"}]';
  //  var obj = JSON.parse(s);
  //  createDeviceList(obj);

};

function about()
{
  document.getElementById("home").style.display = "none";
  document.getElementById("control").style.display = "none";
  document.getElementById("setting").style.display = "none";
  document.getElementById("about").style.display = "block";
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
            s += "<div class='rows text-center' style='margin-top:10px;'>"+
            "<span >Thiết Bị: "+ name +" </span> <br/>"+
            //"<span onclick='sendControl(this)' data-id='"+id+"' data-name='status'><input id='status_"+id+"' data-id='"+id+"' name='status' type='checkbox' checked data-onstyle='info'/></span> "+
            "<div><button data-id='?"+id+"=CMD1' onclick='sendControl(this)' data-name='on' type='button' class='btn btn-warning'>ON</button> "+
            "<button data-id='?DV"+id+"=CMD2' onclick='sendControl(this)' data-name='off' type='button' class='btn btn-warning'>OFF</button> "+
            "<button data-id='?DV"+id+"=CMD3' onclick='sendControl(this)' data-name='dec' type='button' class='btn btn-warning'>--</button> "+
            "<button data-id='?DV"+id+"=CMD4' onclick='sendControl(this)' data-name='inc' type='button' class='btn btn-warning'>++</button> "+
            "<button data-id='?DV"+id+"=CMD5' onclick='sendControl(this)' data-name='tem' type='button' class='btn btn-success'>TEMP</button> </div> <br/>"+
            "<button data-id='?DV"+id+"=CMD6' onclick='sendControl(this)' data-name='tmr' type='button' class='btn btn-danger'>Timer +</button> "+
            "<button data-id='?DV"+id+"=CMD7' onclick='sendControl(this)' data-name='tof' type='button' class='btn btn-danger'>Timer OFF</button> "+
            "<button data-id='?DV"+id+"=CMD8' onclick='sendControl(this)' data-name='mod' type='button' class='btn btn-success'>Mode</button> "+
            "</div><hr/>";
        }
    }
    else
    {
        s = "<div class='rows text-center' style='margin-top:10px;'><h3 style='color:red; font-weight:bold;'>No Device Found!</h3></div>";
        Android.sendData("UPDATE");
    }
    //console.log(s);
    document.getElementById('divContent').innerHTML  = s;

    for(var i = 0; i< lsDevice.length; i++)
    {
      var id = '#status_'+(lsDevice[i].deviceId);
      $(id).bootstrapToggle();
    }
};

// Tạo bảng thiết bị
function getDeviceList(lsDevice)
{
   //console.log(lsDevice);
   var s = "";
   s += "<table class='table table-hover table-border text-center'>";
   s += "<tr>";
   s += "<thead><td>STT</td><td>Device Id</td><td>Device Name</td></thead>";
   s += "</tr>";

     if (lsDevice != null && lsDevice.length > 0)
     {
        for(var i = 0; i< lsDevice.length; i++)
        {
          s += "<tr onclick='showModal(this)'><td>"+(i+1)+"</td><td>"+lsDevice[i].deviceId +"</td><td>"+lsDevice[i].deviceName +"</td></tr>";
        }
     }
   s += "</table>";
   console.log("getDeviceList: s = "+ s);
   document.getElementById('deviceList').innerHTML  = s;

   // Clear input
   document.getElementById('newId').value = "";
   document.getElementById('newName').value = "";
   // Tạo control điều khiển
   initControl(lsDevice);
};

function showModal(row)
{
  var id = row.cells[1].innerHTML;
  var name = row.cells[2].innerHTML;
  console.log("updateName id: "+id);
  console.log("updateName name: "+name);
  document.getElementById('deviceOldId').value = id;
  document.getElementById('deviceNewId').value = id;
  document.getElementById('deviceName').value = name;
  $('#modalDevice').modal('show');
};

function updateDevice()
  {
    var newid = document.getElementById('deviceNewId').value;
    var oldid = document.getElementById('deviceOldId').value;
    var name = document.getElementById('deviceName').value;
    if(newid.trim().length == 0)
    {
        Android.showToast("Device Id is empty.");
        return;
    }
    if(name.trim().length == 0)
    {
        Android.showToast("Device Name is empty.");
        return;
    }
    Android.updateDevice(newid, oldid,name);
    $('#modalDevice').modal('hide');
  };

function deleteDevice()
 {
    var id = document.getElementById('deviceNewId').value;
    var name = document.getElementById('deviceName').value;
    // Gọi hàm bên java để xóa thiết
    Android.deleteteDevice(id);
    $('#modalDevice').modal('hide');
  };

function createDevice()
{
    var id = document.getElementById('newId').value;
    var name = document.getElementById('newName').value;
    if(id.trim().length == 0)
    {
        Android.showToast("Device Id is empty.");
        return;
    }
    if(name.trim().length == 0)
    {
        Android.showToast("Device Name is empty.");
        return;
    }
    // Gọi hàm bên java để thêm thiết bị mới vào DB
    Android.createDevice(id, name);
};

function showIpPort(ip,port)
{
    try
    {
        console.log("start showIpPort");
        var s = ("showIpPort: "+ ip + "-" + port);
        console.log(s);
        document.getElementById('ipaddress').value = ip;
        document.getElementById('port').value = port;
    }
    catch(e)
    {
         console.log("showIpPort: "+ e);
    }
};

function connectServer(){
     var ip = document.getElementById('ipaddress').value;
     var port = document.getElementById('port').value;
     var s = "btnConnect: "+ ip + "-" + port;

     console.log(s);
     if(ip.trim().length == 0)
     {
        document.getElementById('errorMessage').innerHTML = "Ip not correct";
        return;
     }
     if(port.trim().length == 0)
     {
         document.getElementById('errorMessage').innerHTML = "port not correct";
         return;
     }

     Android.connect(ip, port);
};

function sendControl(btnControl)
{
   var name = btnControl.getAttribute('data-name');
   var id = btnControl.getAttribute('data-id');
   //console.log(name);
   console.log("sendControl: "+ id);
   Android.sendData(id);
   //Android.sendData("TB1_BTN1");
};

function updateAll()
{
    Android.sendData("UPDATE");
};

// Đọc hoặc ghi thông tin của 1 thiết bị giữa ESP và APP
// Gửi lệnh xuống ESP để lấy thông tin của thiết bị hiện tại
// Xử lý lưu thông tin thiết bị nhận được trong hàm receive
function readOne()
{
    // Gửi id thiết bị xuống ESP để yêu cầu lấy thông tin thiết bị
    var newid = document.getElementById('deviceOldId').value;
    Android.sendData("DOWNLOAD="+newid);
};

// Ghi thông tin thiết bị xuống ESP
function writeOne()
{
    var newid = document.getElementById('deviceNewId').value;
    var oldid = document.getElementById('deviceOldId').value;
    var name = document.getElementById('deviceName').value;
    Android.sendData("UPLOAD="+newid+","+name);
};
//-----------------------------------------------------

function readAll()
{
    // Gửi id thiết bị xuống ESP để yêu cầu lấy thông tin thiết bị
    var newid = document.getElementById('deviceOldId').value;
    Android.sendData("DOWNLOAD=ALL");
};

// Ghi thông tin thiết bị xuống ESP
function writeAll()
{
    var newid = document.getElementById('deviceNewId').value;
    var oldid = document.getElementById('deviceOldId').value;
    var name = document.getElementById('deviceName').value;
    Android.sendData("UPLOAD=ALL");
};