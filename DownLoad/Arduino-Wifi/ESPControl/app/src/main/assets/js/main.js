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
            var address = lsDevice[i].deviceAddress;
            s += "<div class='rows text-center' style='margin-top:10px;'>"+
            "<span >Thiết Bị: "+ name +" - "+ address +"</span> <br/>"+
            //"<span onclick='sendControl(this)' data-id='"+id+"' data-name='status'><input id='status_"+id+"' data-id='"+id+"' name='status' type='checkbox' checked data-onstyle='info'/></span> "+
            "<div><button data-id='1' data-address='"+address+"' onclick='sendControl(this)' data-name='on' type='button' class='btn btn-warning'><span class='glyphicon glyphicon-ok'></span></button> "+
            "<button data-id='2' data-address='"+address+"' onclick='sendControl(this)' data-name='off' type='button' class='btn btn-warning'><span class='glyphicon glyphicon-off'></span></button> "+
            "<button data-id='3' data-address='"+address+"' onclick='sendControl(this)' data-name='dec' type='button' class='btn btn-warning'><span class='glyphicon glyphicon-minus'></span></button> "+
            "<button data-id='4' data-address='"+address+"' onclick='sendControl(this)' data-name='inc' type='button' class='btn btn-warning'><span class='glyphicon glyphicon-plus'></span></button> "+
            "<button data-id='5' data-address='"+address+"' onclick='sendControl(this)' data-name='tmr' type='button' class='btn btn-danger'><span class='glyphicon glyphicon-time'></span></button> "+
            "<button data-id='6' data-address='"+address+"' onclick='sendControl(this)' data-name='tof' type='button' class='btn btn-danger'><span class='glyphicon glyphicon-calendar'></span></button> "+
            "<button data-id='7' data-address='"+address+"' onclick='sendControl(this)' data-name='tem' type='button' class='btn btn-success'><span class='glyphicon glyphicon-fire'></span></button> "+
            "<button data-id='8' data-address='"+address+"' onclick='sendControl(this)' data-name='mod' type='button' class='btn btn-success'><span class='glyphicon glyphicon-cog'></span></button> "+
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
   s += "<thead><td>STT</td><td>Device Id</td><td>Device Name</td><td>Device Address</td></thead>";
   s += "</tr>";

     if (lsDevice != null && lsDevice.length > 0)
     {
        for(var i = 0; i< lsDevice.length; i++)
        {
          s += "<tr onclick='showModal(this)'><td>"+(i+1)+"</td><td>"+lsDevice[i].deviceId +"</td><td>"+lsDevice[i].deviceName +"</td><td>"+lsDevice[i].deviceAddress +"</td></tr>";
        }
     }
   s += "</table>";
   console.log("getDeviceList: s = "+ s);
   document.getElementById('deviceList').innerHTML  = s;

   // Clear input
   document.getElementById('newId').value = "";
   document.getElementById('newName').value = "";
   document.getElementById('newAddress').value = "";
   // Tạo control điều khiển
   initControl(lsDevice);
};

function showModal(row)
{
  var id = row.cells[1].innerHTML;
  var name = row.cells[2].innerHTML;
  var address = row.cells[3].innerHTML;
  console.log("updateName id: "+id);
  console.log("updateName name: "+name);
  document.getElementById('deviceOldId').value = id;
  document.getElementById('deviceNewId').value = id;
  document.getElementById('deviceName').value = name;
  document.getElementById('deviceAddress').value = address;
  $('#modalDevice').modal('show');
};

function updateDevice()
  {
    var oldid = document.getElementById('deviceOldId').value;
    var id = document.getElementById('deviceId').value;
    var name = document.getElementById('deviceName').value;
    var address = document.getElementById('deviceAddress').value;
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
    var obj = new Object();
               obj.deviceId = id;
               obj.deviceName  = name;
               obj.deviceAddress = address;
               var jsonDevice= JSON.stringify(obj);
    Android.updateDevice(oldid,jsonDevice);
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
    var address = document.getElementById('newAddress').value;
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
    var obj = new Object();
               obj.deviceId = id;
               obj.deviceName  = name;
               obj.deviceAddress = address;
               var jsonDevice= JSON.stringify(obj);
    Android.createDevice(jsonDevice);
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
   var address = btnControl.getAttribute('data-address');
   var id = btnControl.getAttribute('data-id');
   var s= "";
   s += "{123456,CTL,";
   s += address;
   s += ",CMD,";
   s+= id;
   console.log("sendControl: "+ s);
   Android.sendData(s);
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