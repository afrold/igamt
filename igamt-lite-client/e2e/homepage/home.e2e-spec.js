'use strict';

/* https://github.com/angular/protractor/blob/master/docs/toc.md */

describe('IGAMT home page', function() {

  it('should automatically redirect to /home the first time', function() {
    // expect(true).toBe(true);
    browser.get('/');
    browser.waitForAngular();
    expect(browser.getLocationAbsUrl())
      .toBe('/home');
  });

  it('should display welcome page', function() {
    browser.get('#/home');
    browser.waitForAngular();
    var EC = protractor.ExpectedConditions;
    var welcome = element.all(by.css('.igamt-home'));
    browser.wait(EC.presenceOf(welcome), 5000);
    expect(welcome.get(0).getText()).toContain('Welcome to the Implementation Guide Authoring and Management Tool');
   });


  it('should have all the cards present', function() {
    browser.get('#/home');
    browser.waitForAngular();
    var EC = protractor.ExpectedConditions;
    var list = element.all(by.css('div.home-page-toolbar > h2.md-flex'));
    browser.wait(EC.presenceOf(list), 5000);
    expect(list.get(0).getText()).toBe('Note to Users');
    expect(list.get(1).getText()).toBe('Have a Question?');
    expect(list.get(2).getText()).toBe('Supported Browsers');
  });


});
