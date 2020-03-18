$(document).ready(function() {
    // Gọi hàm bên Java, đọc danh sách các thiết bị
    Android.getFloorList();
    $("#btnOpenInsert").click(OpenInsertModal);
    $("#btnCreate").click(create);
    $("#btnUpdate").click(update);
    $("#btnDelete").click(deleteFloor);
});
/* -------------   Chức năng lấy về danh sách thiết bị -------------------*/
// Tạo bảng tầng
function showFloorList(lsFloor)
{
   var s = "";
   s += "<table class='table table-hover table-border text-center'>";
   s += "<tr>";
   s += "<thead><td>STT</td><td>Mã tầng</td><td>Tên tầng</td><td>ip</td><td>port</td></thead>";   //<td>pass</td>
   s += "</tr>";

     if (lsFloor != null && lsFloor.length > 0)
     {
        for(var i = 0; i< lsFloor.length; i++)
        {
          s += "<tr onclick='updateFloor(this)'><td>"+(i+1)+"</td><td>"+lsFloor[i].floorId +"</td><td>"+lsFloor[i].floorName +"</td><td>"+lsFloor[i].floorIp +"</td><td>"+lsFloor[i].floorPort +"</td></tr>";   // <td>"+lsFloor[i].PassWord +"</td>
        }
     }
   s += "</table>";
   console.log("getFloorList: s = "+ s);
   document.getElementById('floorList').innerHTML  = s;

   $('#modalFloor').modal('hide');
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
    $("#titleModal").html("Thêm mới tầng");

    // Clear input
    $("#floorOldId").val("");
    $("#floorId").val("");
    $("#floorName").val("");
    $("#floorIp").val("");
    $("#floorPort").val("");
    // Show modal
	$('#modalFloor').modal('show');
};

function updateFloor(row) {

    $("#btnDelete").show();
    $("#btnUpdate").show();
    $("#btnCreate").hide();
    $("#titleModal").html("Sửa thông tin tầng");

    var id = row.cells[1].innerHTML;
    var name = row.cells[2].innerHTML;
    var ip = row.cells[3].innerHTML;
    var port = row.cells[4].innerHTML;

    //console.log("update floor: "+id+ "; name: "+ name+ "; address: "+ address);

    $('#floorOldId').val(id);
    $('#floorId').val(id);
    $('#floorName').val(name);
    $('#floorIp').val(ip);
    $('#floorPort').val(port);

	$('#modalFloor').modal('show');
};

// Thêm mới thiết bị. Gửi thông tin thiết bị mới sang Java để insert.
function create() {
    try	
	{
		var id = $('#floorId').val();
	    var name = $('#floorName').val();
	    var ip = $('#floorIp').val();
	    var port = $('#floorPort').val();

	    var obj = new Object();
           obj.floorId = id;
           obj.floorName  = name;
           obj.floorIp = ip;
           obj.floorPort = port;
           var jsonFloor = JSON.stringify(obj);
	    Android.createFloor(jsonFloor);
	}
    catch(ex)
    {
    	Console.log('Update Floor Error: '+ ex);
    }
};

// Sửa thông tin thiết bị. 
function update() {
	try	
	{
	    var oldid = $('#floorOldId').val();
		var id = $('#floorId').val();
	    var name = $('#floorName').val();
	    var ip = $('#floorIp').val();
	    var port = $('#floorPort').val();

	    var obj = new Object();
           obj.floorId = id;
           obj.floorName  = name;
           obj.floorIp = ip;
           obj.floorPort = port;
           var jsonFloor = JSON.stringify(obj);
	    Android.updateFloor(oldid, jsonFloor);
	}
    catch(ex)
    {
    	Console.log('Update Floor Error: '+ ex);
    }
};

// Xóa thiết bị
function deleteFloor()
{
    var id = document.getElementById('floorId').value;
    // Gọi hàm bên java để xóa thiết
    Android.deleteFloor(id);
    $('#modalFloor').modal('hide');
};

