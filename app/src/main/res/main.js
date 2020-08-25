Parse.Cloud.beforeSave(Parse.User, async (request) => {

   const query = new Parse.Query("User");

   query.descending("postBox");

   const userObject = await query.first({ useMasterKey: true });

   var highestNumber = userObject.get("postBox");
   request.log.info("Highest postBox is : " + highestNumber);
   var requestPostBox = request.object.get("postBox");

   if(!requestPostBox || requestPostBox <=0) {
      request.object.set("postBox", highestNumber + 1 );
    }
   request.log.info("Setting postBox : " + request.object.get("postBox"));

});

//Parse.Cloud.beforeSave(Parse.Session, async (request) => {
//
//   request.log.info("Inside before save of Parse installation");
//   const query = new Parse.Query("Installation");
//   const userId = request.object.get("userId");
//   query.equalTo("userId", userId);
//   query.notEqualTo("installationId", request.object.get("installationId"));
//
//   const results = await query.find({useMasterKey:true});
//
//   results.forEach(object => {
//
//   request.log.info("Going to set userId null for installation object : " + object.get("objectId"));
//   object.unset("userId");
//   object.save({useMasterKey: true});
//   });
//
//});

//Parse.Cloud.afterDelete(Parse.Session, async(request) => {
//
//   request.log.info("Inside after delete of Parse Session");
//   const query = new Parse.Query("Installation");
//   query.equalTo("installationId", request.object.get("installationId"));
//
//   const results = await query.find({useMasterKey:true});
//
//   results.forEach(object => {
//
//   request.log.info("Going to set userId null for installation object : " + object.get("objectId"));
//   object.unset("userId");
//   object.save({useMasterKey: true});
//   });
//});

Parse.Cloud.job("letterStatusJob", async (request) => {
  let today = new Date();
  let todayWithoutHours = new Date();
  todayWithoutHours.setHours(0,0,0,0);

    request.log.info("Inside letter status job");


  // The query object
  let query = new Parse.Query("LetterMetadata");

  // Query the letters that are unsent
  query.equalTo("Status", "QUEUED_TO_SEND");


  const results = await query.find({useMasterKey:true});

  results.forEach(object => {

    var dateSent = object.get("DateSent");

    var daysInTransit = object.get("daysInTransit");
    var toBeReceivedDate = new Date(dateSent);
    toBeReceivedDate.setDate(toBeReceivedDate.getDate() + daysInTransit);
    var toPostBox = object.get("ToPostBox");
    var letterTitle = object.get("Title");

    toBeReceivedDate.setHours(0,0,0,0);

    request.log.info("Inside for" + "To Be received date(without hours) is : " + toBeReceivedDate.getTime());
    request.log.info("Today without hours  is : " + todayWithoutHours.getTime());


    if(toBeReceivedDate <= todayWithoutHours)
    {
       request.log.info("Inside if");
       object.set("Status", "SENT");
       object.set("DateReceived", today);
       object.save({useMasterKey: true}).then(saved => {


       request.log.info("Successfully saved object" + JSON.stringify(saved));
       request.log.info("To post box is : " + toPostBox);
       let query = new Parse.Query(Parse.User);
       query.equalTo("postBox", toPostBox);
       query.find({useMasterKey:true}).then(function(userResults) {
       request.log.info("Result length is:" + userResults.length);
       request.log.info("Got user as" + userResults[0].get("username"));



       var query = new Parse.Query(Parse.Installation);
       query.equalTo("userId", userResults[0]);

       Parse.Push.send({
            where: query,
            data: {
            alert: "You have got mail!: " + letterTitle,
            name: "New Letter"
            }
          }, { useMasterKey: true } );


//
         });

      }).catch(error => {
          request.log.error("Error: " + error.code + " - " + error.message);
      });




      }
  });



  return ("Successfully retrieved " + results.length + " unsent letters.");
});

//Parse.Cloud.define("sendPushToUser", async (request) => {
//    var query = new Parse.Query(Parse.Installation);
//    let userId = request.object.get("objectId");
//
//    query.equalTo("userId", request.object);
//
// return Parse.Push.send({
//  where: query,
//  data: {
//    alert: "Ricky Vaughn was injured in last night's game!",
//    name: "Vaughn"
//  }
//}, { useMasterKey: true } );
//});


Parse.Cloud.define("pushsample", (request) => {

  request.log.info("Inside function pushSample");
    return Parse.Push.send({
        channels: ["News"],
        data: {
            title: "Hello from the Cloud Code",
            alert: "Back4App rocks!",
        }
    }, { useMasterKey: true });
});