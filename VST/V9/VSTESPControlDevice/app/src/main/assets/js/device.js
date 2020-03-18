$(document).ready(function() {
    // Gọi hàm bên Java, đọc danh sách các thiết bị
    Android.getDevicesList();
    $("#btnOpenInsert").click(OpenInsertModal);
    $("#btnCreate").click(create);
    $("#btnUpdate").click(update);
    $("#btnBack").click(backToSetting);
    //$("#btnDelete").click(deleteDevice);
});

function backToSetting()
{
     window.location.href = "file:///android_asset/www/setting.html";
};

/* -------------   Chức năng lấy về danh sách thiết bị -------------------*/
// Tạo bảng thiết bị
function showDeviceList(lsDevice)
{
   //console.log("danh sách thiết bị: "+ lsDevice);
   var s = "";
   s += "<table class='table table-hover table-border text-center'>";
   s += "<tr>";
   s += "<thead><td>Mã thiết bị</td><td>Tên thiết bị</td><td>Địa chỉ</td><td>Loại thiết bị</td></thead>";
   s += "</tr>";

     if (lsDevice != null && lsDevice.length > 0)
     {
        for(var i = 0; i< lsDevice.length; i++)
        {
          var type = lsDevice[i].deviceType;
          var typeName = "";
          if(type == "11")
            typeName = "A1";
          else if(type == "23")
            typeName = "B3";
          s += "<tr onclick='updateDevice(this)'><td>"+lsDevice[i].deviceId +"</td><td>"+lsDevice[i].deviceName +"</td><td>"+lsDevice[i].deviceAddress +"</td><td>"+ typeName +"</td></tr>";
        }
     }
   s += "</table>";
   console.log("getDeviceList: s = "+ s);
   document.getElementById('deviceList').innerHTML  = s;

   $('#modalDevice').modal('hide');
};

function setType(self)
{
   var cmd = self.innerText;
   if(cmd == "A1")
        $("#deviceType").val("11");
   else if(cmd == "B3")
        $("#deviceType").val("23");
}

/* -------------   Chức năng thêm sửa xóa --------------------------*/

// Mở Modal Device: modalDevice thực hiện chức năng thêm mới thiết bị
// Ẩn: inputOldId, btnUpdate
// Show: btnCreate
// Cách khác: $("#id").css("display", "none");    $("#id").css("display", "block");
function OpenInsertModal() {

    $("#btnCreate").show();
    $("#btnUpdate").hide();
    $("#btnDelete").hide();
    $("#titleModal").html("Thêm mới thiết bị");

    // Clear input
    $("#deviceOldId").val("");
    $("#deviceId").val("");
    $("#deviceName").val("");
    $("#deviceAddress").val("");
    $("#deviceType").val("");
    // Show modal
	$('#modalDevice').modal('show');
};

function updateDevice(row) {

    $("#btnDelete").show();
    $("#btnUpdate").show();
    $("#btnCreate").hide();
    $("#titleModal").html("Sửa thông tin thiết bị");

    var id = row.cells[0].innerHTML;
    var name = row.cells[1].innerHTML;
    var address = row.cells[2].innerHTML;
    var type = row.cells[3].innerHTML;

    console.log("update Device: "+id+ "; name: "+ name+ "; address: "+ address+ "; type: "+ type);

    $('#deviceOldId').val(id);
    $('#deviceId').val(id);
    $('#deviceName').val(name);
    $('#deviceAddress').val(address);
   if(type == "A1")
        type = "11";
   else if(type == "B3")
        type = "23";
    $("#deviceType").val(type);

	$('#modalDevice').modal('show');
};

// Thêm mới thiết bị. Gửi thông tin thiết bị mới sang Java để insert.
function create() {
    try	
	{
		var id = $('#deviceId').val();
	    var name = $('#deviceName').val();
	    var address = $('#deviceAddress').val();
	    var type = $('#deviceType').val();

	    var obj = new Object();
           obj.deviceId = id;
           obj.deviceName  = name;
           obj.deviceAddress = address;
           obj.deviceType = type;
           var jsonDevice= JSON.stringify(obj);
	    Android.createDevice(jsonDevice);
	}
    catch(ex)
    {
    	Console.log('Update device Error: '+ ex);
    }
};

// Sửa thông tin thiết bị. 
function update() {
	try	
	{
	    var oldid = $('#deviceOldId').val();
	    var id = $('#deviceId').val();
	    var name = $('#deviceName').val();
	    var address = $('#deviceAddress').val();
        var type = $('#deviceType').val();

        var obj = new Object();
           obj.deviceId = id;
           obj.deviceName  = name;
           obj.deviceAddress = address;
           obj.deviceType = type;
           var jsonDevice= JSON.stringify(obj);
	    Android.updateDevice(oldid, jsonDevice);
	}
    catch(ex)
    {
    	Console.log('Update device Error: '+ ex);
    }
};

// Xóa thiết bị
function deleteDevice()
{
    console.log('call delete!');
    var id = $('#deviceId').val();
    var type = $('#deviceType').val();
    // Gọi hàm bên java để xóa thiết
    Android.deleteDevice(id,type);
    $('#modalDevice').modal('hide');
};

