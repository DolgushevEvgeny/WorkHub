var formidable = require("formidable");
var fs = require("fs");
var url = require("url");
var http = require("http");
var express = require('express');
var ObjectId = require('mongodb').ObjectID;
var MongoClient = require('mongodb').MongoClient;

var app = express();

app.listen(3000, function(err) {
  console.log("Server has started.");
});

app.get('/getOffices', function(request, response) {
  console.log("Request get /getOffices received.");

  var city = request.query.city;
  var userID = request.query.userID;
  console.log(city);
  console.log(userID);

  var answer = {};
  MongoClient.connect("mongodb://localhost:27017/workhubDB", function(err, db) {
    if (!err) {
      var usersCollection = db.collection('users');
      usersCollection.findOne({'_id': ObjectId(userID)}, function(err, record) {
        if (record) {
          var officesCollection = db.collection('offices');
          officesCollection.find({'city': city}).toArray(function(err, records) {
            if (records) {
              console.log('Кол-во офисов: ' + records.length);
              if (records.length) {
                answer.records = records;
                answer.code = 1;
                db.close();
                sendResponse(answer, response);
              } else {
                answer.code = 2;
                answer.message = 'В данном городе пока еще нет офисов.';
                db.close();
                sendResponse(answer, response);
              }
            } else {
              answer.code = 2;
              answer.message = 'В данном городе пока еще нет офисов.';
              db.close();
              sendResponse(answer, response);
            }
          });
        } else {
          answer.code = 0;
          answer.message = 'Такого пользователя не существует.';
          db.close();
          sendResponse(answer, response);
        }
      });
    }
  });
});

app.get('/getPlans', function(request, response) {
  console.log("Request get /getPlans received.");

  var cityName = request.query.city;
  var officeName = request.query.office;
  var userID = request.query.userID;
  console.log(cityName);
  console.log(officeName);
  console.log(userID);

  var answer = {};
  MongoClient.connect("mongodb://localhost:27017/workhubDB", function(err, db) {
    if (!err) {
      var usersCollection = db.collection('users');
      usersCollection.findOne({'_id': ObjectId(userID)}, function(err, record) {
        if (record) {
          var plansCollection = db.collection('plans');
          plansCollection.find({'city': cityName, 'office': officeName}).toArray(function(err, records) {
            if (records) {
              console.log('Кол-во планов: ' + records.length);
              if (records.length) {
                answer.records = records;
                answer.code = 1;
                db.close();
                sendResponse(answer, response);
              } else {
                answer.code = 2;
                answer.message = 'В данном офисе пока еще нет планов.';
                db.close();
                sendResponse(answer, response);
              }
            } else {
              answer.code = 2;
              answer.message = 'В данном офисе пока еще нет планов.';
              db.close();
              sendResponse(answer, response);
            }
          });
        } else {
          answer.code = 0;
          answer.message = 'Такого пользователя не существует.';
          db.close();
          sendResponse(answer, response);
        }
      });
    }
  });
});

app.get('/login', function(request, response) {
  console.log("Request get /login received.");

  var userLogin = request.query.login;
  var userPassword = request.query.password;
  console.log(userLogin);
  console.log(userPassword);

  var answer = {};
  MongoClient.connect("mongodb://localhost:27017/workhubDB", function(err, db) {
    if (!err) {
      var usersCollection = db.collection('users');
      usersCollection.findOne({'login': userLogin, 'password': userPassword}, function(err, record) {
        if (!err) {
          if (record) {
            console.log(record);
            answer.id = record._id;
            db.close();
            sendResponse(answer, response);
          } else {
            console.log('Записи нет');
            answer.id = -1;
            db.close();
            sendResponse(answer, response);
          }
        }
      });
    }
  });
});

app.get('/getDayWorkTime', function(request, response) {
  console.log("Request get /getDayWorkTime received.");

  var day = request.query.day;
  var officeName = request.query.office;
  var cityName = request.query.city;
  console.log(day);
  console.log(officeName);
  console.log(cityName);

  var answer = {};
  MongoClient.connect("mongodb://localhost:27017/workhubDB", function(err, db) {
    if (!err) {
      var officesCollection = db.collection('offices');
      officesCollection.findOne({'city': cityName, 'name': officeName}, function(err, record) {
        if (!err) {
          if (record) {
            var workList = record.workList;
            var resultDay = workList[day];
            answer.work_day = resultDay;
            answer.code = 1;
            db.close();
            sendResponse(answer, response);
          }
        } else {
          //Todo обработать ошибку
        }
      });
    }
  });
});

app.get('/canMakeReservation', function(request, response) {
  console.log("Request get /canMakeReservation received.");

  var officeName = request.query.office,
      cityName = request.query.city,
      planName = request.query.plan,
      date = request.query.date,
      startTime = +request.query.startTime,
      duration = +request.query.duration,
      planPrice = +request.query.planPrice,
      userID = request.query.userID;

  console.log(officeName);
  console.log(cityName);
  console.log(planName);
  console.log(date);
  console.log(startTime);
  console.log(duration);
  console.log(planPrice);
  console.log(userID);

  var answer = {};
  var list = [];

  var listTimes = [];
  for (var i = startTime; i < startTime + duration; ++i) {
    listTimes.push(i);
  }

  MongoClient.connect("mongodb://localhost:27017/workhubDB", function(err, db) {
    if (!err) {
      var plansCollection = db.collection('plans');
      plansCollection.findOne({'city': cityName, 'office': officeName,
        'name': planName}, function(err, record) {
        if (!err) {
          var planCapacity = +record.capacity;
          console.log('plan capacity: ' + planCapacity);

          var reservationsCollection = db.collection('reservations');
          reservationsCollection.find({'city': cityName, 'office': officeName,'plan': planName,
            'date': date}).toArray(function(err, records) {
            console.log(err);
            console.log(records);
            if (records) {
              console.log('всего бронирований на день: ' + records.length);
              for (var i = 0; i < records.length; ++i) {
                var itemStartTime = +records[i].startTime,
                    itemDuration = +records[i].duration;

                console.log(itemStartTime);
                console.log(itemDuration);

                for (var j = itemStartTime; j < itemStartTime + itemDuration; ++j) {
                  var listItem = getListItem(list, j);
                  listItem.hour = j;
                  listItem.count += 1;
                  list = setListItem(list, listItem, j);
                }
              }

              console.log(list);
              var canReserve = true;
              var nonReserveTime = 0;
              for (var i = 0; i < listTimes.length; ++i) {
                var listItem = getListItem(list, listTimes[i]);
                if (listItem.count < planCapacity) {
                  listItem.count += 1;
                  setListItem(list, listItem, listTimes[i]);
                } else {
                  canReserve = false;
                  nonReserveTime = listTimes[i];
                  for (var j = 0; j < i; ++j) {
                    removeReserve(list, listTimes[j])
                  }
                  break;
                }
              }
              canReserve ? console.log('Можно занять') : console.log('Нельзя занять');
              if (canReserve) {
                setReservation(reservationsCollection, cityName, officeName, planName, date, startTime, duration, planPrice, userID);
                answer.code = 1;
              } else {
                answer.code = 0;
                answer.nonReserve = nonReserveTime;
              }

              db.close();
              sendResponse(answer, response);
            } else {
              setReservation(reservationsCollection, cityName, officeName, planName, date, startTime, duration, planPrice, userID);
              answer.code = 1;

              db.close();
              sendResponse(answer, response);
            }
          });
        }
      });
    }
  });
});

app.get('/removeReservation', function(request, response) {
  console.log("Request get /canMakeReservation received.");

  var officeName = request.query.office,
      cityName = request.query.city,
      planName = request.query.plan,
      date = request.query.date,
      startTime = +request.query.startTime,
      duration = +request.query.duration,
      userID = request.query.userID;

  var answer = {};
  MongoClient.connect("mongodb://localhost:27017/workhubDB", function(err, db) {
    if (!err) {
      var reservationsCollection = db.collection('reservations');
      reservationsCollection.remove({'city': cityName, 'office': officeName,'plan': planName,
        'date': date, 'startTime': startTime, 'duration': duration, 'userID': userID}, 1);
    }
    db.close();
    sendResponse(answer, response);
  });
});

app.get('/MyReservations', function(request, response) {
  console.log("Request get /canMakeReservation received.");
  var userID = request.query.userID;

  var answer = {};
  MongoClient.connect("mongodb://localhost:27017/workhubDB", function(err, db) {
    if (!err) {
      var reservationsCollection = db.collection('reservations');
      reservationsCollection.find({'userID': userID}).toArray(function(err, records) {
        if (records) {
          if (records.length) {
            answer.count = records.length;
            answer.records = records;
            db.close();
            sendResponse(answer, response);
          } else {
            answer.count = 0;
            db.close();
            sendResponse(answer, response);
          }
        } else {
          answer.count = 0;
          db.close();
          sendResponse(answer, response);
        }
      });
    }
  });
});

function getListItem(list, hour) {
  for (var i = 0; i < list.length; ++i) {
    if (list[i].hour == hour) {
      return list[i];
    }
  }
  return {'hour': 0, 'count': 0};
}

function setListItem(list, item, hour) {
  for (var i = 0; i < list.length; ++i) {
    if (list[i].hour == hour) {
      list[i] = item;
      return list;
    }
  }
  list.push(item);
  return list;
}

function removeReserve(list, hour) {
  for (var i = 0; i < list.length; ++i) {
    if (list[i].hour == hour) {
      list[i].count -= 1;
      return;
    }
  }
}

function setReservation(collection, cityName, officeName, planName, date, startTime, duration, planPrice, userID) {
  var record = setReservationFields(cityName, officeName, planName, date, startTime, duration, planPrice, userID);
  collection.insertOne(record);
}

function setReservationFields(cityName, officeName, planName, date, startTime, duration, planPrice, userID) {
  var reservation = {};
  reservation.city = cityName;
  reservation.office = officeName;
  reservation.plan = planName;
  reservation.date = date;
  reservation.startTime = startTime;
  reservation.duration = duration;
  reservation.planPrice = planPrice;
  reservation.userID = userID;
  return reservation;
}

function sendResponse(answer, response) {
  response = setResponseHeaders(response);
  response.setHeader('Content-Type', 'application/json');
  response.json(answer);
}

function setResponseHeaders(response) {
  response.setHeader("Access-Control-Allow-Origin", "*");
  response.setHeader("Access-Control-Allow-Headers", "X-Requested-With");
  response.setHeader("Access-Control-Allow-Headers", "Content-Type");
  return response;
}
