$(document).ready(function() {
    // Gọi hàm bên Java, đọc danh sách các thiết bị
    Android.getDevicesList();
    $("#btnOpenInsert").click(OpenInsertModal);
    $("#btnCreate").click(create);
    $("#btnUpdate").click(update);
    $("#btnDelete").click(deleteDevice);
});
/* -------------   Chức năng lấy về danh sách thiết bị -------------------*/
// Tạo bảng thiết bị
function showDeviceList(lsDevice)
{
   //console.log(lsDevice);
   var s = "";
   s += "<table class='table table-hover table-border text-center'>";
   s += "<tr>";
   s += "<thead><td>STT</td><td>Mã thiết bị</td><td>Tên thiết bị</td><td>Địa chỉ</td></thead>";
   s += "</tr>";

     if (lsDevice != null && lsDevice.length > 0)
     {
        for(var i = 0; i< lsDevice.length; i++)
        {
          s += "<tr onclick='updateDevice(this)'><td>"+(i+1)+"</td><td>"+lsDevice[i].deviceId +"</td><td>"+lsDevice[i].deviceName +"</td><td>"+lsDevice[i].deviceAddress +"</td></tr>";
        }
     }
   s += "</table>";
   console.log("getDeviceList: s = "+ s);
   document.getElementById('deviceList').innerHTML  = s;

   $('#modalDevice').modal('hide');
};

/* -------------   Tạo list các Control --------------------------*/
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
            "<div><button data-id='?"+id+"=CMD1' onclick='sendControl(this)' data-name='on' type='button' class='btn btn-warning'><span class='glyphicon glyphicon-ok'></span></button> "+
            "<button data-id='?DV"+id+"=CMD2' onclick='sendControl(this)' data-name='off' type='button' class='btn btn-warning'><span class='glyphicon glyphicon-off'></span></button> "+
            "<button data-id='?DV"+id+"=CMD3' onclick='sendControl(this)' data-name='dec' type='button' class='btn btn-warning'><span class='glyphicon glyphicon-minus'></span></button> "+
            "<button data-id='?DV"+id+"=CMD4' onclick='sendControl(this)' data-name='inc' type='button' class='btn btn-warning'><span class='glyphicon glyphicon-plus'></span></button> "+
            "<button data-id='?DV"+id+"=CMD5' onclick='sendControl(this)' data-name='tem' type='button' class='btn btn-success'><span class='glyphicon glyphicon-fire'></span></button> </div> <br/>"+
            "<button data-id='?DV"+id+"=CMD6' onclick='sendControl(this)' data-name='tmr' type='button' class='btn btn-danger'><span class='glyphicon glyphicon-time'></span></button> "+
            "<button data-id='?DV"+id+"=CMD7' onclick='sendControl(this)' data-name='tof' type='button' class='btn btn-danger'><span class='glyphicon glyphicon-calendar'></span></button> "+
            "<button data-id='?DV"+id+"=CMD8' onclick='sendControl(this)' data-name='mod' type='button' class='btn btn-success'><span class='glyphicon glyphicon-cog'></span></button> "+
            "</div><hr/>";
        }
    }
    else
    {
        s = "<div class='rows text-center' style='margin-top:10px;'><h3 style='color:red; font-weight:bold;'>No Device Found!</h3></div>";
        Android.sendData("UPDATE");
    }

    for(var i = 0; i< lsDevice.length; i++)
    {
      var id = '#status_'+(lsDevice[i].deviceId);
      $(id).bootstrapToggle();
    }
};

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
    // Show modal
	$('#modalDevice').modal('show');
};

function updateDevice(row) {

    $("#btnDelete").show();
    $("#btnUpdate").show();
    $("#btnCreate").hide();
    $("#titleModal").html("Sửa thông tin thiết bị");

    var id = row.cells[1].innerHTML;
    var name = row.cells[2].innerHTML;
    var address = row.cells[3].innerHTML;

    console.log("update Device: "+id+ "; name: "+ name+ "; address: "+ address);

    $('#deviceOldId').val(id);
    $('#deviceId').val(id);
    $('#deviceName').val(name);
    $('#deviceAddress').val(address);

	$('#modalDevice').modal('show');
};

// Thêm mới thiết bị. Gửi thông tin thiết bị mới sang Java để insert.
function create() {
    try	
	{
		var id = $('#deviceId').val();
	    var name = $('#deviceName').val();
	    var address = $('#deviceAddress').val();
	    var obj = new Object();
           obj.deviceId = id;
           obj.deviceName  = name;
           obj.deviceAddress = address;
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

        var obj = new Object();
           obj.deviceId = id;
           obj.deviceName  = name;
           obj.deviceAddress = address;
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
    var id = document.getElementById('deviceId').value;
    // Gọi hàm bên java để xóa thiết
    Android.deleteDevice(id);
    $('#modalDevice').modal('hide');
};

