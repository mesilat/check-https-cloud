module.exports = function (app, addon) {

  // Root route. This route will serve the `atlassian-connect.json` unless the
  // documentation url inside `atlassian-connect.json` is set
  app.get('/', function (req, res) {
    res.format({
      'text/html': function () {
        res.redirect('/atlassian-connect.json');
      },
      'application/json': function () {
        res.redirect('/atlassian-connect.json');
      }
    });
  });

  app.get('/macro', addon.authenticate(), function (req, res) {
    if (req.query['lic'] === 'active') {
      res.render('macro.hbs', {
        host: req.query['host'],
        port: req.query['port']
      });
    } else {
      res.render('nolicense.hbs');
    }
  });

  // load any additional files you have in routes and apply those to the app
  {
    var fs = require('fs');
    var path = require('path');
    var files = fs.readdirSync("routes");
    for (var index in files) {
      var file = files[index];
      if (file === "index.js") continue;
      // skip non-javascript files
      if (path.extname(file) != ".js") continue;

      var routes = require("./" + path.basename(file));

      if (typeof routes === "function") {
        routes(app, addon);
      }
    }
  }
};
