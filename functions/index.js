const functions = require('firebase-functions');
const scrape = require('./scrape.js');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
exports.helloWorld = functions.https.onRequest((request, response) => {

   scrape.then(noticeArray=>{  return response.send(noticeArray);})//resolved promise
          .catch(err=>console.log(err));
});
