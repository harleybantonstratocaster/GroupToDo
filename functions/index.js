const condition = '\'stock-GOOG\' in topics || \'industry-tech\' in topics';

// See documentation on defining a message payload.
const message = {
  notification: {
    title: '$FooCorp up 1.43% on the day',
    body: '$FooCorp gained 11.80 points to close at 835.67, up 1.43% on the day.'
  },
  condition: condition
};

// Send a message to devices subscribed to the combination of topics
// specified by the provided condition.
getMessaging().send(message)
  .then((response) => {
    // Response is a message ID string.
    console.log('Successfully sent message:', response);
  })
  .catch((error) => {
    console.log('Error sending message:', error);
  });