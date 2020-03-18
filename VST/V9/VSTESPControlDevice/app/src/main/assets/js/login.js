$(document).ready(function() {
    Android.getPassLogin();

    $("#chkShowHide").click(function() {
        if ($(this).is(':checked')) {
            //console.log("chkShowHide_click: true");
            $('#pass').prop('type', 'text');
        } else {
            //console.log("chkShowHide_click: false");
            $('#pass').prop('type', 'password');
        }
    });

    $("#chkSavePass").click(function() {
        console.log("chkSavePass_click");
        var cmd = "";
        if ($(this).is(':checked'))
            cmd = "YES";
        else
            cmd = "NO";
        Android.save_unsave(cmd);
    });
});
// Gửi lệnh đăng nhập
function Login() {
    var pass = $("#pass").val();
    Android.login(pass);
    $("#waitImage").html(imgPathOne);
};
// Hiển thị pass của lần đăng nhập cuối cùng.
function showPassLogin(pass) {
    $("#pass").val(pass);
};
// đăng nhập thành công thì chuyển sang bên giao diện điều khiển, ẩn ảnh động
function loginSuccess() {
    $("#waitImage").html();
    window.location.href = "file:///android_asset/www/control.html";
};
// đăng nhập thất bại
function loginFail() {
    $("#waitImage").html("");
    $("#waitImage").append("<h3>Đăng nhập thất bại!</h3>");
};

function backToStart() {
    Android.backToStart();
    window.location.href = "file:///android_asset/www/start.html";
};