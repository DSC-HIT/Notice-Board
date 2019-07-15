const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp(functions.config().firebase);

exports.pushNotification = functions.database
  .ref("/data1/{motto}/{dept}/{year}/{key}")
  .onCreate((snapshot, context) => {
    console.log("Push notification triggered!");
    console.log(context.params);

    var valueObject = snapshot.val();
    // Create a notification
    const payload = {
      notification: {
        title: valueObject.type,
        body: valueObject.lable + " " + valueObject.description,
        sound: "default"
      }
    };

    //Create an options object that contains the time to live for the notification and the priority
    const options = {
      priority: "high",
      timeToLive: 60 * 60 * 24
    };

    return admin
      .messaging()
      .sendToTopic(
        `pushNotifications_${context.params.motto}_${context.params.dept}_${
          context.params.year
        }`,
        payload,
        options
      );
  });
