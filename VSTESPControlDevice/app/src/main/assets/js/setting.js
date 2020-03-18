
function backToControl()
{
     window.location.href = "file:///android_asset/www/control.html";
};

$("#btnFloor").click(function() {
    window.location.href = "file:///android_asset/www/floor.html";
});

$("#btnDevice").click(function() {
    window.location.href = "file:///android_asset/www/device.html";
});

$("#btnpLogin").click(function() {
    $("#typeChangePass").val("login");
    $("#modalChangePass").modal('show');

});

$("#btnpSetup").click(function() {
    $("#typeChangePass").val("setup");
    $("#modalChangePass").modal('show');
});

$("#btnDangXuat").click(function(){
    window.location.href = "file:///android_asset/www/start.html";
});

$( "#btnLogout" ).click(function() {
    $("#modalLogout").modal('show');
});

function changePass()
{
    var old = $("#in_old_pass").val();
    var new1 = $("#in_new_pass1").val();
    var new2 = $("#in_new_pass2").val();

    if(new1 != new2)
    {
        Android.showToast("Mật khẩu mới không trùng nhau!");
        return;
    }
    var type = $("#typeChangePass").val();
    if(type == "login")
        Android.changePass(1, old, new1);
    else if(type == "setup")
        Android.changePass(2, old, new1);
};

function changeLoginPass()
{

};