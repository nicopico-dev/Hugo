'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.updateLastAction_DEV = functions.firestore
    .document('dev_users/{userId}/babies/{baby}/timeline/{entry}')
    .onCreate((snapshot, context) => {
        const userId = context.params.userId;
        return admin.firestore()
            .doc(`dev_users/${userId}`)
            .set(
                { lastAction: context.timestamp }, 
                { merge: true }
            );
    });