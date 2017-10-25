import {Component,Inject,forwardRef} from '@angular/core';
import {AppComponent} from './app.component';

@Component({
    selector: 'app-footer',
    styleUrls: ['./app.footer.component.css'],
    template: `
      <div id="footer" class="region region-footer ">
        <div class="footer__inner">

          <div class="social-links">
            <div class="item-list">
              <ul>
                <li ><a href="https://twitter.com/USNISTGOV" target="_blank" class="social-btn social-btn--large extlink ext"><i class="faa faa-twitter"><span class="element-invisible">twitter</span></i><span class="ext"><span class="element-invisible"> (link is external)</span></span></a></li>
                <li class="field-item service-facebook list-horiz"><a href="https://www.facebook.com/USNISTGOV" target="_blank" class="social-btn social-btn--large extlink ext"><i class="faa faa-facebook"><span class="element-invisible">facebook</span></i><span class="ext"><span class="element-invisible"> (link is external)</span></span></a></li>
                <li class="field-item service-googleplus list-horiz"><a href="https://plus.google.com/+USNISTGOV" target="_blank" class="social-btn social-btn--large extlink ext"><i class="faa faa-google-plus"><span class="element-invisible">google plus</span></i><span class="ext"><span class="element-invisible"> (link is external)</span></span></a></li>
                <li class="field-item service-youtube list-horiz"><a href="https://www.youtube.com/user/USNISTGOV" target="_blank" class="social-btn social-btn--large extlink ext"><i class="faa faa-youtube"><span class="element-invisible">youtube</span></i><span class="ext"><span class="element-invisible"> (link is external)</span></span></a></li>
                <li class="field-item service-rss list-horiz"><a href="https://www2.nist.gov/news-events/nist-rss-feeds" target="_blank" class="social-btn social-btn--large extlink"><i class="faa faa-rss"><span class="element-invisible">rss</span></i></a></li>
                <li class="field-item service-govdelivery list-horiz last"><a href="https://service.govdelivery.com/accounts/USNIST/subscriber/new" target="_blank" class="social-btn social-btn--large extlink ext"><i class="faa faa-envelope"><span class="element-invisible">govdelivery</span></i><span class="ext"><span class="element-invisible"> (link is external)</span></span></a></li>
              </ul>
            </div>
          </div>

          <div class="footer__logo">
            <a href="" title="National Institute of Standards and Technology" class="footer__logo-link" rel="home">
              <img srcset="./assets/images/logo_rev.png" alt="National Institute of Standards and Technology" title="National Institute of Standards and Technology">
            </a>
          </div>

          <div class="footer__contact">
            <p>
              <strong>HEADQUARTERS</strong><br>
              100 Bureau Drive<br>
              Gaithersburg, MD 20899
            </p>
            <p>
              <a href="https://www.nist.gov/about-nist/contact-us" target="_blank"><u>Contact Us</u></a> | <a href="https://www.nist.gov/about-nist/our-organization" target="_blank"><u>Our Other Offices</u></a>
            </p>
          </div>

          <div id="block-menu-menu-footer-menu" class="block menu--footer-menu first even block--menu block--menu-menu-footer-menu" role="navigation">
            <ul class="menu"><li class="menu__item is-leaf first leaf menu-depth-1"><a href="https://www.nist.gov/privacy-policy" target="_blank" class="menu__link">Privacy Statement</a></li>
              <li class="menu__item is-leaf leaf menu-depth-1"><a href="https://www.nist.gov/privacy-policy#privpolicy" target="_blank" class="menu__link"> Privacy Policy</a></li>
              <li class="menu__item is-leaf leaf menu-depth-1"><a href="https://www.nist.gov/privacy-policy#secnot" target="_blank" class="menu__link"> Security Notice</a></li>
              <li class="menu__item is-leaf leaf menu-depth-1"><a href="https://www.nist.gov/privacy-policy#accesstate" target="_blank" class="menu__link"> Accessibility Statement</a></li>
              <li class="menu__item is-leaf leaf menu-depth-1"><a href="https://www.nist.gov/privacy" target="_blank" class="menu__link"> NIST Privacy Program</a></li>
              <li class="menu__item is-leaf last leaf menu-depth-1"><a href="https://www.nist.gov/no-fear-act-policy" target = "_blank" class="menu__link"> No Fear Act Policy</a></li>
            </ul>
          </div>
          <div id="block-menu-menu-footer-menu-2" class="block menu--footer-menu odd block--menu block--menu-menu-footer-menu-2" role="navigation">
            <ul class="menu"><li class="menu__item is-leaf first leaf menu-depth-1"><a href="https://www.nist.gov/disclaimer" target="_blank" class="menu__link"> Disclaimer</a></li>
              <li class="menu__item is-leaf leaf menu-depth-1"><a href="https://www.nist.gov/office-director/freedom-information-act" target="_blank" class="menu__link"> FOIA</a></li>
              <li class="menu__item is-leaf leaf menu-depth-1"><a href="https://www.nist.gov/environmental-policy-statement" target="_blank" class="menu__link"> Environmental Policy Statement</a></li>
              <li class="menu__item is-leaf leaf menu-depth-1"><a href="https://www.nist.gov/privacy-policy#cookie" target="_blank" class="menu__link"> Cookie Disclaimer</a></li>
              <li class="menu__item is-leaf leaf menu-depth-1"><a href="https://www.nist.gov/summary-report-scientific-integrity" target="_blank" class="menu__link"> Scientific Integrity Summary</a></li>
              <li class="menu__item is-leaf last leaf menu-depth-1"><a href="https://www.nist.gov/nist-information-quality-standards" target="_blank" class="menu__link"> NIST Information Quality Standards</a></li>
            </ul>
          </div>
          <div id="block-menu-menu-footer-menu-3" class="block menu--footer-menu last even block--menu block--menu-menu-footer-menu-3" role="navigation">
            <ul class="menu"><li class="menu__item is-leaf first leaf menu-depth-1"><a href="http://business.usa.gov/" class="menu__link ext extlink" target="_blank">Business USA<span class="ext"><span class="element-invisible"> (link is external)</span></span></a></li>
              <li class="menu__item is-leaf leaf menu-depth-1"><a href="https://www.healthcare.gov/" class="menu__link ext extlink" target="_blank"> Healthcare.gov<span class="ext"><span class="element-invisible"> (link is external)</span></span></a></li>
              <li class="menu__item is-leaf leaf menu-depth-1"><a href="http://www.science.gov/" class="menu__link ext extlink" target="_blank"> Science.gov<span class="ext"><span class="element-invisible"> (link is external)</span></span></a></li>
              <li class="menu__item is-leaf last leaf menu-depth-1"><a href="http://www.usa.gov/" class="menu__link ext extlink" target="_blank"> USA.gov<span class="ext"><span class="element-invisible"> (link is external)</span></span></a></li>
            </ul>
          </div>
        </div>
      </div>
    `
})
export class AppFooterComponent {

}
