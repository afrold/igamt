'use strict';

/* https://github.com/angular/protractor/blob/master/docs/toc.md */

describe('IGAMT home page', function() {

  beforeEach(function()
  {
    browser.get('#/home');
    browser.waitForAngular();
  });

  it('should automatically redirect to /home when location hash/fragment is empty', function() {
    expect(browser.getCurrentUrl()).toContain("#/home");
  });

});
