//this module returns a promise after scraping the notice board of the website.

const cheerio = require('cheerio');
const request = require('request');

function scrapeNoticeBoard(){
    return new Promise(resolve=>{
        request({
            method:'GET',
            url:'http://heritageit.edu/'
        },(err,res)=>{
            if(err){
                console.log(err);
            }else{
                var $ = cheerio.load(res.body);
                var notices = [];
               $('#notice table tbody tr td table tbody ').children('tr').each((i,el)=>{
                    var title = $(el).find('td span').eq(0).text();
                    var a = $(el).find('a');
                    var link = $(a).attr('href');
                    notices[i] = {title:title,link:link};//array of objects to store notice data
               });
                 resolve(notices);//promise returned
            }
        });
    });

}

module.exports = scrapeNoticeBoard();
