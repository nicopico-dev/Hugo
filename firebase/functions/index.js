const functions = require('firebase-functions');

exports.sendWelcomeEmail = functions.auth.user().onCreate((user) => {
  // ...
});