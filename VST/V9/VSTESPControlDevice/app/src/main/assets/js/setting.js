$(document).ready(function() {
    $("#btnCheck").prop("readonly",true);
});

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

$("#btnLogin").click(function() {
    $("#typeChangePass").val("login");
    $("#modalChangePass").modal('show');

});

$("#btnSetup").click(function() {
    $("#typeChangePass").val("setup");
    $("#modalChangePass").modal('show');
});

$("#btnDangXuat").click(function(){
    Android.logout();
});

$( "#btnLogout" ).click(function() {
    $("#modalLogout").modal('show');
});

function changePass()
{
    var old = $("#in_old_pass").val();
    var new1 = $("#in_new_pass1").val();
    var new2 = $("#in_new_pass2").val();

    var type = $("#typeChangePass").val();
    if(type == "login")
        Android.changePass(1, old, new1, new2);
    else if(type == "setup")
        Android.changePass(2, old, new1, new2);
};

function validatePass()
  {
      var pass = $("#in_new_pass1").val();
       // If x is Not a Number or less than one or greater than 10
      if (isNaN(pass) || pass.length < 6 || pass.length > 6) {
          $("#passalert").val("Mật khẩu chỉ gồm 6 kí tự số.");
          //$("#btnCheck").prop("readonly",true);
      } else {
          $("#passalert").val()
          //$("#btnCheck").prop("readonly",false);
      }
  };

  function comparePass()
  {
       var new1 = $("#in_new_pass1").val();
       var new2 = $("#in_new_pass2").val();
       if(new1 != new2)
       {
            $("#passalert").val("Mật khẩu không khớp");
            return;
       }
       else
       {
            $("#btnCheck").prop("readonly",false);
       }
  };