$(function () {
   //Android.ShowToast('kq: cc ');
   getCurrentHistoryData();
   getToolStatus();
   setInterval(getCurrentHistoryData, 5000);
   setTimeout(function(){  }, 2000);
   setInterval(getToolStatus, 60000);
   $('#tool_one').change(function(){
        var tb1Status = $(this).prop('checked');
        updateToolStatus(tb1Status);
        if(tb1Status== true)
            $('#toolStatus').val('1');
        else
            $('#toolStatus').val('0');
   });
});

function sendSMS(){
      setTimeout(function(){
         var tb1Status = $('#toolStatus').val();
        if(tb1Status== '1')
            Android.ControlPump(true);
        else if(tb1Status== '0')
            Android.ControlPump(false);

      }, 2000);
};

function getCurrentHistoryData()
{
  try{
     $.ajax({
       type: 'GET',
       url: 'http://ais.tnut.edu.vn/Android/getCurentHistoryData?deviceId=1&callback=?',
       //data:{'deviceId':1},
       success: function (jsonObject) {
            //Android.ShowToast('kq: '+html);
           if (jsonObject === null || jsonObject === "" || jsonObject === "[]" || jsonObject == "undefined") {
               return;
           }

          console.log(jsonObject);
           var temp = jsonObject["ValueTemperature"];
           var humi = jsonObject["ValueHumidity"];
           var water = jsonObject["WaterLevel"];
           var power = jsonObject["Power"];
           $('#temperature').html(parseFloat(temp).toFixed(2));
           $('#humidity').html(parseFloat(humi).toFixed(2));
           $('#water_level').html(water);
           $('#power').html(power);
       },
       error: function(e) {
         //console.log(e); 
         $('#temperature').html('NaN');
         $('#humidity').html('NaN');
         $('#water_level').html('NaN');
         $('#power').html('NaN');
       }
   }); 
  }
  catch(Exception)
  {
    $('#temperature').html('NaN'); 
    $('#humidity').html('NaN');
    $('#water_level').html('NaN');
    $('#power').html('NaN');
  }
};

function getToolStatus()
{
  try{
     $.ajax({
       type: 'GET',
       url: 'http://ais.tnut.edu.vn/Android/getToolStatus?regionId=1&callback=?',
       //data:{'regionId':1},
       success: function (html) {
           if (html === null || html === "" || html === "[]" || html == "undefined") {
               return;
           }
           //var jsonObject = $.parseJSON(html); 
           console.log(html);
           var jsonObject = JSON.parse(html);             
           if (jsonObject != null) {
             var tb1 = jsonObject["tb1"];
             if(tb1 == true)
                $('#tool_one').bootstrapToggle('on');
             else
                $('#tool_one').bootstrapToggle('off');
           }                     
       },
       error: function myfunction(e) { 
         //console.log(e); 

       }
   }); 
  }
  catch(Exception)
  {

  }
};

function updateToolStatus(tb1Status)
{
    try{
         $.ajax({
           type: 'GET',
           url: 'http://ais.tnut.edu.vn/Android/updateToolStatus?tb1='+tb1Status+'&tb2=false&tb3=false&callback=?',
           //data:{'regionId':1},
           success: function (html) {
               console.log('Updated Status');
           },
           error: function(e) {
              console.log('Cannot Update Status');
           }
       });
      }
      catch(Exception)
      {
           console.log('Cannot Update Status');
      }
};

function updateHistory(jsonHistory)
{
    try{
        console.log(json);
        var jsonType = typeof jsonHistory;
        var temp, humi, timeIrrgation;
        if(jsonType == 'string')
        {
            var jsonObject = JSON.parse(jsonHistory);
            temp = jsonObject["ValueTemperature"];
            humi = jsonObject["ValueHumidity"];
            timeIrrgation = jsonObject["TimeIrrgation"];
        }
        else if(jsonType == 'object')
        {
            temp = jsonHistory["ValueTemperature"];
            humi = jsonHistory["ValueHumidity"];
            timeIrrgation = jsonHistory["TimeIrrgation"];
        }

         $.ajax({
           type: 'GET',
           url: 'http://ais.tnut.edu.vn/Android/insertHistory?deviceId=1&valueHumidity='+humi+'&valueTemperature='+temp+'&timeIrrgation='+timeIrrgation+'&callback=?',
           success: function (html) {
               console.log('Updated History!');
           },
           error: function(e) {
              console.log('Can not update History!');
           }
       });
    }
    catch(Exception)
    {
       console.log('Cannot Update History');
    }
};