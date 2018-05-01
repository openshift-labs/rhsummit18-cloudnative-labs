'use strict';

/*
 *
 *  Copyright 2016-2017 Red Hat, Inc, and individual contributors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

const fs = require('fs');
const express = require('express');
const path = require('path');
const bodyParser = require('body-parser');
const inventorydb = JSON.parse(fs.readFileSync('inventory.json', 'utf8'));
const SERVICE_DELAY = process.env.SERVICE_DELAY || 0;
const app = express();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(express.static(path.join(__dirname, 'public')));

var misbehave = false;

app.get("/misbehave", (req, res) => {
  misbehave = true;
  res.sendStatus(204).end();
});
app.get("/behave", (req, res) => {
  misbehave = false;
  res.sendStatus(204).end();
});

app.use('/services/inventory/:itemId', (request, response) => {
  
  if (misbehave) {
    response.sendStatus(503).end();
    return;
  }

  const  itemId = request.params.itemId;

  setTimeout(function() {

    if (itemId === 'all') {
      console.log(new Date() + " - GET /services/inventory/all: 200 OK");
      return response.json(inventorydb);
    }

    const inventory_result = inventorydb.find(function(el) {
      return el.itemId === itemId;
    });
    if (inventory_result) {
      console.log(new Date() + " - GET /services/inventory/" + itemId + ": 200 OK" + " after a delay of " + SERVICE_DELAY);
      return response.json(inventory_result);
    } else {
      console.log(new Date() + " - GET /services/inventory/" + itemId + ": 404 Not Found");
      response.sendStatus(404).end();
    }
  }, SERVICE_DELAY);
  
});


module.exports = app;
