//driver module to test the scrape module.
//change console.log() statement to whatever is needed


const scrape = require('./scrape');

 scrape.then(noticeArray=>{  console.log(noticeArray);})//resolved promise
        .catch(err=>console.log(err));