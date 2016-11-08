<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" encoding="utf-8"
		doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
		doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
		indent="yes" />
	<xsl:param name="inlineConstraints" select="'false'"></xsl:param>

	<xsl:template match="/">

		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
				<title>Implementation guide</title>
				<style type="text/css">
					body,
					html {
					font-family: 'Arial Narrow',
					sans-serif;
					font-size: 10px;
					width:100%;
					overflow:
					auto;
					max-height: 100vh;
					}
				</style>
				<style type="text/css" >
/*!
 * froala_editor v2.3.5 (https://www.froala.com/wysiwyg-editor)
 * License https://froala.com/wysiwyg-editor/terms/
 * Copyright 2014-2016 Froala Labs
 */

.clearfix::after{clear:both;display:block;content:"";height:0}.fr-element,.fr-element:focus{outline:0 solid transparent}.fr-box.fr-basic .fr-element{text-align:initial;color:#000;padding:10px;-webkit-box-sizing:border-box;-moz-box-sizing:border-box;box-sizing:border-box;overflow-x:auto;min-height:40px}.fr-element{background:0 0;position:relative;z-index:2;-webkit-user-select:auto}.fr-element a{user-select:auto;-o-user-select:auto;-moz-user-select:auto;-khtml-user-select:auto;-webkit-user-select:auto;-ms-user-select:auto}.fr-element.fr-disabled{user-select:none;-o-user-select:none;-moz-user-select:none;-khtml-user-select:none;-webkit-user-select:none;-ms-user-select:none}.fr-element [contenteditable=false]{user-select:none;-o-user-select:none;-moz-user-select:none;-khtml-user-select:none;-webkit-user-select:none;-ms-user-select:none}.fr-element [contenteditable=true]{outline:0 solid transparent}.fr-box a.fr-floating-btn{-webkit-box-shadow:0 1px 3px rgba(0,0,0,.12),0 1px 2px rgba(0,0,0,.24);-moz-box-shadow:0 1px 3px rgba(0,0,0,.12),0 1px 2px rgba(0,0,0,.24);box-shadow:0 1px 3px rgba(0,0,0,.12),0 1px 2px rgba(0,0,0,.24);border-radius:100%;-moz-border-radius:100%;-webkit-border-radius:100%;-moz-background-clip:padding;-webkit-background-clip:padding-box;background-clip:padding-box;height:32px;width:32px;background:#fff;color:#1e88e5;-webkit-transition:background .2s ease 0s,color .2s ease 0s,transform .2s ease 0s;-moz-transition:background .2s ease 0s,color .2s ease 0s,transform .2s ease 0s;-ms-transition:background .2s ease 0s,color .2s ease 0s,transform .2s ease 0s;-o-transition:background .2s ease 0s,color .2s ease 0s,transform .2s ease 0s;outline:0;left:0;top:0;line-height:32px;-webkit-transform:scale(0);-moz-transform:scale(0);-ms-transform:scale(0);-o-transform:scale(0);text-align:center;display:block;-webkit-box-sizing:border-box;-moz-box-sizing:border-box;box-sizing:border-box;border:0}.fr-box a.fr-floating-btn svg{-webkit-transition:transform .2s ease 0s;-moz-transition:transform .2s ease 0s;-ms-transition:transform .2s ease 0s;-o-transition:transform .2s ease 0s;fill:#1e88e5}.fr-box a.fr-floating-btn i{font-size:14px;line-height:32px}.fr-box a.fr-floating-btn.fr-btn+.fr-btn{margin-left:10px}.fr-box a.fr-floating-btn:hover{background:#ebebeb;cursor:pointer}.fr-box a.fr-floating-btn:hover svg{fill:#1e88e5}.fr-box .fr-visible a.fr-floating-btn{-webkit-transform:scale(1);-moz-transform:scale(1);-ms-transform:scale(1);-o-transform:scale(1)}iframe.fr-iframe{width:100%;border:0;position:relative;display:block;z-index:2;-webkit-box-sizing:border-box;-moz-box-sizing:border-box;box-sizing:border-box}.fr-wrapper{position:relative;z-index:1}.fr-wrapper::after{clear:both;display:block;content:"";height:0}.fr-wrapper .fr-placeholder{position:absolute;font-size:12px;color:#aaa;z-index:1;display:none;top:0;left:0;overflow:hidden}.fr-wrapper.show-placeholder .fr-placeholder{display:block}.fr-wrapper ::selection{background:#b5d6fd;color:#000}.fr-wrapper ::-moz-selection{background:#b5d6fd;color:#000}.fr-box.fr-rtl .fr-wrapper .fr-placeholder{right:0;left:auto}.fr-box.fr-basic .fr-wrapper{background:#fff;border:0;border-top:0;top:0;left:0}.fr-box.fr-basic.fr-rtl .fr-wrapper .fr-placeholder{right:0;left:auto}.fr-box.fr-basic.fr-top .fr-wrapper{border-top:0;border-radius:0 0 2px 2px;-moz-border-radius:0 0 2px 2px;-webkit-border-radius:0 0 2px 2px;-moz-background-clip:padding;-webkit-background-clip:padding-box;background-clip:padding-box;-webkit-box-shadow:0 1px 3px rgba(0,0,0,.12),0 1px 2px rgba(0,0,0,.24);-moz-box-shadow:0 1px 3px rgba(0,0,0,.12),0 1px 2px rgba(0,0,0,.24);box-shadow:0 1px 3px rgba(0,0,0,.12),0 1px 2px rgba(0,0,0,.24)}.fr-box.fr-basic.fr-bottom .fr-wrapper{border-bottom:0;border-radius:2px 2px 0 0;-moz-border-radius:2px 2px 0 0;-webkit-border-radius:2px 2px 0 0;-moz-background-clip:padding;-webkit-background-clip:padding-box;background-clip:padding-box;-webkit-box-shadow:0 -1px 3px rgba(0,0,0,.12),0 -1px 2px rgba(0,0,0,.24);-moz-box-shadow:0 -1px 3px rgba(0,0,0,.12),0 -1px 2px rgba(0,0,0,.24);box-shadow:0 -1px 3px rgba(0,0,0,.12),0 -1px 2px rgba(0,0,0,.24)}.fr-tooltip{position:absolute;top:0;left:0;padding:0 8px;border-radius:2px;-moz-border-radius:2px;-webkit-border-radius:2px;-moz-background-clip:padding;-webkit-background-clip:padding-box;background-clip:padding-box;-webkit-box-shadow:0 3px 6px rgba(0,0,0,.16),0 2px 4px rgba(0,0,0,.23);-moz-box-shadow:0 3px 6px rgba(0,0,0,.16),0 2px 4px rgba(0,0,0,.23);box-shadow:0 3px 6px rgba(0,0,0,.16),0 2px 4px rgba(0,0,0,.23);background:#222;color:#fff;font-size:11px;line-height:22px;font-family:Arial,Helvetica,sans-serif;-webkit-transition:opacity .2s ease 0s;-moz-transition:opacity .2s ease 0s;-ms-transition:opacity .2s ease 0s;-o-transition:opacity .2s ease 0s;-webkit-opacity:0;-moz-opacity:0;opacity:0;-ms-filter:"alpha(Opacity=0)";left:-3000px;user-select:none;-o-user-select:none;-moz-user-select:none;-khtml-user-select:none;-webkit-user-select:none;-ms-user-select:none;z-index:9997}.fr-tooltip.fr-visible{-webkit-opacity:1;-moz-opacity:1;opacity:1;-ms-filter:"alpha(Opacity=0)"}.fr-toolbar{color:#222;background:#fff;position:relative;z-index:4;font-family:Arial,Helvetica,sans-serif;-webkit-box-sizing:border-box;-moz-box-sizing:border-box;box-sizing:border-box;user-select:none;-o-user-select:none;-moz-user-select:none;-khtml-user-select:none;-webkit-user-select:none;-ms-user-select:none;padding:0 2px;border-radius:2px;-moz-border-radius:2px;-webkit-border-radius:2px;-moz-background-clip:padding;-webkit-background-clip:padding-box;background-clip:padding-box;-webkit-box-shadow:0 1px 3px rgba(0,0,0,.12),0 1px 2px rgba(0,0,0,.24);-moz-box-shadow:0 1px 3px rgba(0,0,0,.12),0 1px 2px rgba(0,0,0,.24);box-shadow:0 1px 3px rgba(0,0,0,.12),0 1px 2px rgba(0,0,0,.24);text-align:left;border:0;border-top:5px solid #222}.fr-toolbar::after{clear:both;display:block;content:"";height:0}.fr-toolbar.fr-rtl{text-align:right}.fr-toolbar.fr-inline{display:none;white-space:nowrap;position:absolute;margin-top:10px}.fr-toolbar.fr-inline .fr-arrow{width:0;height:0;border-left:5px solid transparent;border-right:5px solid transparent;border-bottom:5px solid #222;position:absolute;top:-9px;left:50%;margin-left:-5px;display:inline-block}.fr-toolbar.fr-inline.fr-above{margin-top:-10px;-webkit-box-shadow:0 -1px 3px rgba(0,0,0,.12),0 -1px 2px rgba(0,0,0,.24);-moz-box-shadow:0 -1px 3px rgba(0,0,0,.12),0 -1px 2px rgba(0,0,0,.24);box-shadow:0 -1px 3px rgba(0,0,0,.12),0 -1px 2px rgba(0,0,0,.24);border-bottom:5px solid #222;border-top:0}.fr-toolbar.fr-inline.fr-above .fr-arrow{top:auto;bottom:-9px;border-bottom:0;border-top-color:inherit;border-top-style:solid;border-top-width:5px}.fr-toolbar.fr-top{top:0;border-radius:2px 2px 0 0;-moz-border-radius:2px 2px 0 0;-webkit-border-radius:2px 2px 0 0;-moz-background-clip:padding;-webkit-background-clip:padding-box;background-clip:padding-box;-webkit-box-shadow:0 1px 3px rgba(0,0,0,.12),0 1px 2px rgba(0,0,0,.24);-moz-box-shadow:0 1px 3px rgba(0,0,0,.12),0 1px 2px rgba(0,0,0,.24);box-shadow:0 1px 3px rgba(0,0,0,.12),0 1px 2px rgba(0,0,0,.24)}.fr-toolbar.fr-bottom{bottom:0;border-radius:0 0 2px 2px;-moz-border-radius:0 0 2px 2px;-webkit-border-radius:0 0 2px 2px;-moz-background-clip:padding;-webkit-background-clip:padding-box;background-clip:padding-box;-webkit-box-shadow:0 1px 3px rgba(0,0,0,.12),0 1px 2px rgba(0,0,0,.24);-moz-box-shadow:0 1px 3px rgba(0,0,0,.12),0 1px 2px rgba(0,0,0,.24);box-shadow:0 1px 3px rgba(0,0,0,.12),0 1px 2px rgba(0,0,0,.24)}.fr-separator{background:#ebebeb;display:block;vertical-align:top;float:left}.fr-separator+.fr-separator{display:none}.fr-separator.fr-vs{height:33px;width:1px;margin:2px}.fr-separator.fr-hs{clear:both;height:1px;width:calc(100% - (2 * 2px));margin:0 2px}.fr-separator.fr-hidden{display:none!important}.fr-rtl .fr-separator{float:right}.fr-toolbar.fr-inline .fr-separator.fr-hs{float:none}.fr-toolbar.fr-inline .fr-separator.fr-vs{float:none;display:inline-block}.fr-toolbar .fr-command.fr-btn,.fr-popup .fr-command.fr-btn{background:0 0;color:#222;-moz-outline:0;outline:0;border:0;line-height:1;cursor:pointer;text-align:left;margin:0 2px;-webkit-transition:background .2s ease 0s;-moz-transition:background .2s ease 0s;-ms-transition:background .2s ease 0s;-o-transition:background .2s ease 0s;border-radius:0;-moz-border-radius:0;-webkit-border-radius:0;-moz-background-clip:padding;-webkit-background-clip:padding-box;background-clip:padding-box;z-index:2;position:relative;-webkit-box-sizing:border-box;-moz-box-sizing:border-box;box-sizing:border-box;text-decoration:none;user-select:none;-o-user-select:none;-moz-user-select:none;-khtml-user-select:none;-webkit-user-select:none;-ms-user-select:none;float:left;padding:0;width:38px;height:37px}.fr-toolbar .fr-command.fr-btn.fr-btn-text,.fr-popup .fr-command.fr-btn.fr-btn-text{width:auto}.fr-toolbar .fr-command.fr-btn i,.fr-popup .fr-command.fr-btn i{display:block;font-size:15px;width:15px;margin:11px 11.5px;text-align:center;float:none}.fr-toolbar .fr-command.fr-btn span,.fr-popup .fr-command.fr-btn span{font-size:14px;display:block;line-height:14px;min-width:38px;float:left;text-overflow:ellipsis;overflow:hidden;white-space:nowrap;height:15px;font-weight:700;padding:0 2px}.fr-toolbar .fr-command.fr-btn img,.fr-popup .fr-command.fr-btn img{margin:11px 11.5px;width:15px}.fr-toolbar .fr-command.fr-btn.fr-active,.fr-popup .fr-command.fr-btn.fr-active{color:#1e88e5;background:0 0}.fr-toolbar .fr-command.fr-btn.fr-dropdown.fr-selection,.fr-popup .fr-command.fr-btn.fr-dropdown.fr-selection{width:auto}.fr-toolbar .fr-command.fr-btn.fr-dropdown.fr-selection span,.fr-popup .fr-command.fr-btn.fr-dropdown.fr-selection span{font-weight:400}.fr-toolbar .fr-command.fr-btn.fr-dropdown i,.fr-popup .fr-command.fr-btn.fr-dropdown i,.fr-toolbar .fr-command.fr-btn.fr-dropdown span,.fr-popup .fr-command.fr-btn.fr-dropdown span,.fr-toolbar .fr-command.fr-btn.fr-dropdown img,.fr-popup .fr-command.fr-btn.fr-dropdown img{margin-left:7.5px;margin-right:15.5px}.fr-toolbar .fr-command.fr-btn.fr-dropdown.fr-active,.fr-popup .fr-command.fr-btn.fr-dropdown.fr-active{color:#222;background:#d6d6d6}.fr-toolbar .fr-command.fr-btn.fr-dropdown.fr-active:hover,.fr-popup .fr-command.fr-btn.fr-dropdown.fr-active:hover,.fr-toolbar .fr-command.fr-btn.fr-dropdown.fr-active:focus,.fr-popup .fr-command.fr-btn.fr-dropdown.fr-active:focus{background:#d6d6d6!important;color:#222!important}.fr-toolbar .fr-command.fr-btn.fr-dropdown.fr-active:hover::after,.fr-popup .fr-command.fr-btn.fr-dropdown.fr-active:hover::after,.fr-toolbar .fr-command.fr-btn.fr-dropdown.fr-active:focus::after,.fr-popup .fr-command.fr-btn.fr-dropdown.fr-active:focus::after{border-top-color:#222!important}.fr-toolbar .fr-command.fr-btn.fr-dropdown::after,.fr-popup .fr-command.fr-btn.fr-dropdown::after{position:absolute;width:0;height:0;border-left:4px solid transparent;border-right:4px solid transparent;border-top:4px solid #222;right:3.75px;top:16.5px;content:""}.fr-toolbar .fr-command.fr-btn.fr-disabled,.fr-popup .fr-command.fr-btn.fr-disabled{color:#bdbdbd;cursor:default}.fr-toolbar .fr-command.fr-btn.fr-disabled::after,.fr-popup .fr-command.fr-btn.fr-disabled::after{border-top-color:#bdbdbd!important}.fr-toolbar .fr-command.fr-btn.fr-hidden,.fr-popup .fr-command.fr-btn.fr-hidden{display:none}.fr-toolbar.fr-disabled .fr-btn,.fr-popup.fr-disabled .fr-btn,.fr-toolbar.fr-disabled .fr-btn.fr-active,.fr-popup.fr-disabled .fr-btn.fr-active{color:#bdbdbd}.fr-toolbar.fr-disabled .fr-btn.fr-dropdown::after,.fr-popup.fr-disabled .fr-btn.fr-dropdown::after,.fr-toolbar.fr-disabled .fr-btn.fr-active.fr-dropdown::after,.fr-popup.fr-disabled .fr-btn.fr-active.fr-dropdown::after{border-top-color:#bdbdbd}.fr-toolbar.fr-rtl .fr-command.fr-btn,.fr-popup.fr-rtl .fr-command.fr-btn{float:right}.fr-toolbar.fr-inline .fr-command.fr-btn{float:none}.fr-desktop .fr-command:hover,.fr-desktop .fr-command:focus{color:#222;background:#ebebeb}.fr-desktop .fr-command:hover::after,.fr-desktop .fr-command:focus::after{border-top-color:#222!important}.fr-desktop .fr-command.fr-selected{color:#222;background:#d6d6d6}.fr-desktop .fr-command.fr-active:hover,.fr-desktop .fr-command.fr-active:focus{color:#1e88e5;background:#ebebeb}.fr-desktop .fr-command.fr-active.fr-selected{color:#1e88e5;background:#d6d6d6}.fr-desktop .fr-command.fr-disabled:hover,.fr-desktop .fr-command.fr-disabled:focus,.fr-desktop .fr-command.fr-disabled.fr-selected{background:0 0}.fr-desktop.fr-disabled .fr-command:hover,.fr-desktop.fr-disabled .fr-command:focus,.fr-desktop.fr-disabled .fr-command.fr-selected{background:0 0}.fr-toolbar.fr-mobile .fr-command.fr-blink,.fr-popup.fr-mobile .fr-command.fr-blink{background:0 0}.fr-command.fr-btn+.fr-dropdown-menu{display:inline-block;position:absolute;right:auto;bottom:auto;height:auto;-webkit-box-shadow:0 3px 6px rgba(0,0,0,.16),0 2px 4px rgba(0,0,0,.23);-moz-box-shadow:0 3px 6px rgba(0,0,0,.16),0 2px 4px rgba(0,0,0,.23);box-shadow:0 3px 6px rgba(0,0,0,.16),0 2px 4px rgba(0,0,0,.23);z-index:3;-webkit-overflow-scrolling:touch;overflow:hidden;border-radius:0 0 2px 2px;-moz-border-radius:0 0 2px 2px;-webkit-border-radius:0 0 2px 2px;-moz-background-clip:padding;-webkit-background-clip:padding-box;background-clip:padding-box}.fr-command.fr-btn+.fr-dropdown-menu .fr-dropdown-wrapper{background:#fff;-webkit-box-shadow:0 3px 6px rgba(0,0,0,.16),0 2px 4px rgba(0,0,0,.23);-moz-box-shadow:0 3px 6px rgba(0,0,0,.16),0 2px 4px rgba(0,0,0,.23);box-shadow:0 3px 6px rgba(0,0,0,.16),0 2px 4px rgba(0,0,0,.23);padding:0;margin:auto;display:inline-block;text-align:left;position:relative;-webkit-box-sizing:border-box;-moz-box-sizing:border-box;box-sizing:border-box;-webkit-transition:max-height .2s ease 0s;-moz-transition:max-height .2s ease 0s;-ms-transition:max-height .2s ease 0s;-o-transition:max-height .2s ease 0s;margin-top:0;float:left;max-height:0;height:0;margin-top:0!important}.fr-command.fr-btn+.fr-dropdown-menu .fr-dropdown-wrapper .fr-dropdown-content{overflow:auto;position:relative;max-height:275px}.fr-command.fr-btn+.fr-dropdown-menu .fr-dropdown-wrapper .fr-dropdown-content ul.fr-dropdown-list{list-style-type:none;margin:0;padding:0}.fr-command.fr-btn+.fr-dropdown-menu .fr-dropdown-wrapper .fr-dropdown-content ul.fr-dropdown-list li{padding:0;margin:0;font-size:15px}.fr-command.fr-btn+.fr-dropdown-menu .fr-dropdown-wrapper .fr-dropdown-content ul.fr-dropdown-list li a{padding:0 24px;line-height:200%;display:block;cursor:pointer;white-space:nowrap;color:inherit;text-decoration:none}.fr-command.fr-btn+.fr-dropdown-menu .fr-dropdown-wrapper .fr-dropdown-content ul.fr-dropdown-list li a.fr-active{background:#d6d6d6}.fr-command.fr-btn+.fr-dropdown-menu .fr-dropdown-wrapper .fr-dropdown-content ul.fr-dropdown-list li a.fr-disabled{color:#bdbdbd;cursor:default}.fr-command.fr-btn.fr-active+.fr-dropdown-menu{display:inline-block}.fr-command.fr-btn.fr-active+.fr-dropdown-menu .fr-dropdown-wrapper{height:auto;max-height:275px}.fr-bottom>.fr-command.fr-btn+.fr-dropdown-menu{border-radius:2px 2px 0 0;-moz-border-radius:2px 2px 0 0;-webkit-border-radius:2px 2px 0 0;-moz-background-clip:padding;-webkit-background-clip:padding-box;background-clip:padding-box}.fr-toolbar.fr-rtl .fr-dropdown-wrapper,.fr-popup.fr-rtl .fr-dropdown-wrapper{text-align:right!important}.fr-popup{position:absolute;display:none;color:#222;background:#fff;-webkit-box-shadow:0 1px 3px rgba(0,0,0,.12),0 1px 2px rgba(0,0,0,.24);-moz-box-shadow:0 1px 3px rgba(0,0,0,.12),0 1px 2px rgba(0,0,0,.24);box-shadow:0 1px 3px rgba(0,0,0,.12),0 1px 2px rgba(0,0,0,.24);border-radius:2px;-moz-border-radius:2px;-webkit-border-radius:2px;-moz-background-clip:padding;-webkit-background-clip:padding-box;background-clip:padding-box;font-family:Arial,Helvetica,sans-serif;-webkit-box-sizing:border-box;-moz-box-sizing:border-box;box-sizing:border-box;user-select:none;-o-user-select:none;-moz-user-select:none;-khtml-user-select:none;-webkit-user-select:none;-ms-user-select:none;margin-top:10px;z-index:9995;text-align:left;border:0;border-top:5px solid #222}.fr-popup.fr-above{margin-top:-10px;border-top:0;border-bottom:5px solid #222;-webkit-box-shadow:0 -1px 3px rgba(0,0,0,.12),0 -1px 2px rgba(0,0,0,.24);-moz-box-shadow:0 -1px 3px rgba(0,0,0,.12),0 -1px 2px rgba(0,0,0,.24);box-shadow:0 -1px 3px rgba(0,0,0,.12),0 -1px 2px rgba(0,0,0,.24)}.fr-popup.fr-active{display:block}.fr-popup.fr-hidden{-webkit-opacity:0;-moz-opacity:0;opacity:0;-ms-filter:"alpha(Opacity=0)"}.fr-popup .fr-hs{display:block!important}.fr-popup .fr-hs.fr-hidden{display:none!important}.fr-popup .fr-input-line{position:relative;padding:8px 0}.fr-popup .fr-input-line input[type=text],.fr-popup .fr-input-line textarea{width:100%;margin:0 0 1px;border:0;border-bottom:solid 1px #bdbdbd;color:#222;font-size:14px;padding:6px 0 2px;background:rgba(0,0,0,0);position:relative;z-index:2;-webkit-box-sizing:border-box;-moz-box-sizing:border-box;box-sizing:border-box}.fr-popup .fr-input-line input[type=text]:focus,.fr-popup .fr-input-line textarea:focus{border-bottom:solid 2px #1e88e5;margin-bottom:0}.fr-popup .fr-input-line input+label,.fr-popup .fr-input-line textarea+label{position:absolute;top:0;left:0;font-size:12px;color:rgba(0,0,0,0);-webkit-transition:color .2s ease 0s;-moz-transition:color .2s ease 0s;-ms-transition:color .2s ease 0s;-o-transition:color .2s ease 0s;z-index:1}.fr-popup .fr-input-line input.fr-not-empty:focus+label,.fr-popup .fr-input-line textarea.fr-not-empty:focus+label{color:#1e88e5}.fr-popup .fr-input-line input.fr-not-empty+label,.fr-popup .fr-input-line textarea.fr-not-empty+label{color:gray}.fr-popup input,.fr-popup textarea{user-select:text;-o-user-select:text;-moz-user-select:text;-khtml-user-select:text;-webkit-user-select:text;-ms-user-select:text;border-radius:0;-moz-border-radius:0;-webkit-border-radius:0;-moz-background-clip:padding;-webkit-background-clip:padding-box;background-clip:padding-box;outline:0}.fr-popup textarea{resize:none}.fr-popup .fr-buttons{-webkit-box-shadow:0 1px 3px rgba(0,0,0,.12),0 1px 2px rgba(0,0,0,.24);-moz-box-shadow:0 1px 3px rgba(0,0,0,.12),0 1px 2px rgba(0,0,0,.24);box-shadow:0 1px 3px rgba(0,0,0,.12),0 1px 2px rgba(0,0,0,.24);padding:0 2px;white-space:nowrap;line-height:0;border-bottom:0}.fr-popup .fr-buttons::after{clear:both;display:block;content:"";height:0}.fr-popup .fr-buttons .fr-btn{display:inline-block;float:none}.fr-popup .fr-buttons .fr-btn i{float:left}.fr-popup .fr-buttons .fr-separator{display:inline-block;float:none}.fr-popup .fr-layer{width:225px;-webkit-box-sizing:border-box;-moz-box-sizing:border-box;box-sizing:border-box;margin:10px;display:none}@media (min-width:768px){.fr-popup .fr-layer{width:300px}}.fr-popup .fr-layer.fr-active{display:inline-block}.fr-popup .fr-action-buttons{z-index:7;height:36px;text-align:right}.fr-popup .fr-action-buttons button.fr-command{height:36px;line-height:1;color:#1e88e5;padding:10px;cursor:pointer;text-decoration:none;border:0;background:0 0;font-size:16px;outline:0;-webkit-transition:background .2s ease 0s;-moz-transition:background .2s ease 0s;-ms-transition:background .2s ease 0s;-o-transition:background .2s ease 0s}.fr-popup .fr-action-buttons button.fr-command+button{margin-left:24px}.fr-popup .fr-action-buttons button.fr-command:hover,.fr-popup .fr-action-buttons button.fr-command:focus{background:#ebebeb;color:#1e88e5}.fr-popup .fr-action-buttons button.fr-command:active{background:#d6d6d6;color:#1e88e5}.fr-popup .fr-action-buttons button::-moz-focus-inner{border:0}.fr-popup .fr-checkbox{position:relative;display:inline-block;width:16px;height:16px;line-height:1;-webkit-box-sizing:content-box;-moz-box-sizing:content-box;box-sizing:content-box;vertical-align:middle}.fr-popup .fr-checkbox svg{margin-left:2px;margin-top:2px;display:none;width:10px;height:10px}.fr-popup .fr-checkbox span{border:solid 1px #222;border-radius:2px;-moz-border-radius:2px;-webkit-border-radius:2px;-moz-background-clip:padding;-webkit-background-clip:padding-box;background-clip:padding-box;width:16px;height:16px;display:inline-block;position:relative;z-index:1;-webkit-box-sizing:border-box;-moz-box-sizing:border-box;box-sizing:border-box;-webkit-transition:background .2s ease 0s,border-color .2s ease 0s;-moz-transition:background .2s ease 0s,border-color .2s ease 0s;-ms-transition:background .2s ease 0s,border-color .2s ease 0s;-o-transition:background .2s ease 0s,border-color .2s ease 0s}.fr-popup .fr-checkbox input{position:absolute;z-index:2;-webkit-opacity:0;-moz-opacity:0;opacity:0;-ms-filter:"alpha(Opacity=0)";border:0 none;cursor:pointer;height:16px;margin:0;padding:0;width:16px;top:1px;left:1px}.fr-popup .fr-checkbox input:checked+span{background:#1e88e5;border-color:#1e88e5}.fr-popup .fr-checkbox input:checked+span svg{display:block}.fr-popup .fr-checkbox input:focus+span{border-color:#1e88e5}.fr-popup .fr-checkbox-line{font-size:14px;line-height:1.4px;margin-top:10px}.fr-popup .fr-checkbox-line label{cursor:pointer;margin:0 5px;vertical-align:middle}.fr-popup.fr-rtl{direction:rtl;text-align:right}.fr-popup.fr-rtl .fr-action-buttons{text-align:left}.fr-popup.fr-rtl .fr-input-line input+label,.fr-popup.fr-rtl .fr-input-line textarea+label{left:auto;right:0}.fr-popup .fr-arrow{width:0;height:0;border-left:5px solid transparent;border-right:5px solid transparent;border-bottom:5px solid #222;position:absolute;top:-9px;left:50%;margin-left:-5px;display:inline-block}.fr-popup.fr-above .fr-arrow{top:auto;bottom:-9px;border-bottom:0;border-top:5px solid #222}.fr-text-edit-layer{width:250px;-webkit-box-sizing:border-box;-moz-box-sizing:border-box;box-sizing:border-box;display:block!important}.fr-visibility-helper{display:none;margin-left:0!important}@media (min-width:768px){.fr-visibility-helper{margin-left:1px!important}}@media (min-width:992px){.fr-visibility-helper{margin-left:2px!important}}@media (min-width:1200px){.fr-visibility-helper{margin-left:3px!important}}.fr-opacity-0{-webkit-opacity:0;-moz-opacity:0;opacity:0;-ms-filter:"alpha(Opacity=0)"}.fr-box{position:relative}.fr-sticky{position:-webkit-sticky;position:-moz-sticky;position:-ms-sticky;position:-o-sticky;position:sticky}.fr-sticky-off{position:relative}.fr-sticky-on{position:fixed}.fr-sticky-on.fr-sticky-ios{position:absolute;left:0;right:0;width:auto!important}.fr-sticky-dummy{display:none}.fr-sticky-on+.fr-sticky-dummy,.fr-sticky-box>.fr-sticky-dummy{display:block}</style>
				
				 <style type="text/css">
			/*!
 * froala_editor v2.3.5 (https://www.froala.com/wysiwyg-editor)
 * License https://froala.com/wysiwyg-editor/terms/
 * Copyright 2014-2016 Froala Labs
 */

.clearfix::after{clear:both;display:block;content:"";height:0}.fr-view strong{font-weight:700}.fr-view table{border:0;border-collapse:collapse;empty-cells:show;max-width:100%}.fr-view table.fr-dashed-borders td,.fr-view table.fr-dashed-borders th{border-style:dashed}.fr-view table.fr-alternate-rows tbody tr:nth-child(2n){background:#f5f5f5}.fr-view table td,.fr-view table th{border:1px solid #ddd}.fr-view table td:empty,.fr-view table th:empty{height:20px}.fr-view table td.fr-highlighted,.fr-view table th.fr-highlighted{border:1px double red}.fr-view table td.fr-thick,.fr-view table th.fr-thick{border-width:2px}.fr-view table th{background:#e6e6e6}.fr-view hr{clear:both;user-select:none;-o-user-select:none;-moz-user-select:none;-khtml-user-select:none;-webkit-user-select:none;-ms-user-select:none;page-break-after:always}.fr-view .fr-file{position:relative}.fr-view .fr-file::after{position:relative;content:"\1F4CE";font-weight:400}.fr-view pre{white-space:pre-wrap;word-wrap:break-word}.fr-view[dir=rtl] blockquote{border-left:0;border-right:solid 2px #5e35b1;margin-right:0;padding-right:5px;padding-left:0}.fr-view[dir=rtl] blockquote blockquote{border-color:#00bcd4}.fr-view[dir=rtl] blockquote blockquote blockquote{border-color:#43a047}.fr-view blockquote{border-left:solid 2px #5e35b1;margin-left:0;padding-left:5px;color:#5e35b1}.fr-view blockquote blockquote{border-color:#00bcd4;color:#00bcd4}.fr-view blockquote blockquote blockquote{border-color:#43a047;color:#43a047}.fr-view span.fr-emoticon{font-weight:400;font-family:"Apple Color Emoji","Segoe UI Emoji",NotoColorEmoji,"Segoe UI Symbol","Android Emoji",EmojiSymbols;display:inline;line-height:0}.fr-view span.fr-emoticon.fr-emoticon-img{background-repeat:no-repeat!important;font-size:inherit;height:1em;width:1em;min-height:20px;min-width:20px;display:inline-block;margin:-.1em .1em .1em;line-height:1;vertical-align:middle}.fr-view .fr-text-gray{color:#AAA!important}.fr-view .fr-text-bordered{border-top:solid 1px #222;border-bottom:solid 1px #222;padding:10px 0}.fr-view .fr-text-spaced{letter-spacing:1px}.fr-view .fr-text-uppercase{text-transform:uppercase}.fr-view img{position:relative;max-width:100%}.fr-view img.fr-dib{margin:5px auto;display:block;float:none;vertical-align:top}.fr-view img.fr-dib.fr-fil{margin-left:0}.fr-view img.fr-dib.fr-fir{margin-right:0}.fr-view img.fr-dii{display:inline-block;float:none;vertical-align:bottom;margin-left:5px;margin-right:5px;max-width:calc(100% - (2 * 5px))}.fr-view img.fr-dii.fr-fil{float:left;margin:5px 5px 5px 0;max-width:calc(100% - 5px)}.fr-view img.fr-dii.fr-fir{float:right;margin:5px 0 5px 5px;max-width:calc(100% - 5px)}.fr-view img.fr-rounded{border-radius:100%;-moz-border-radius:100%;-webkit-border-radius:100%;-moz-background-clip:padding;-webkit-background-clip:padding-box;background-clip:padding-box}.fr-view img.fr-bordered{border:solid 10px #CCC;-webkit-box-sizing:content-box;-moz-box-sizing:content-box;box-sizing:content-box}.fr-view .fr-video{text-align:center;position:relative}.fr-view .fr-video>*{-webkit-box-sizing:content-box;-moz-box-sizing:content-box;box-sizing:content-box;max-width:100%;border:0}.fr-view .fr-video.fr-dvb{display:block;clear:both}.fr-view .fr-video.fr-dvb.fr-fvl{text-align:left}.fr-view .fr-video.fr-dvb.fr-fvr{text-align:right}.fr-view .fr-video.fr-dvi{display:inline-block}.fr-view .fr-video.fr-dvi.fr-fvl{float:left}.fr-view .fr-video.fr-dvi.fr-fvr{float:right}.fr-view a.fr-strong{font-weight:700}.fr-view a.fr-green{color:green}.fr-view button.fr-rounded,.fr-view input.fr-rounded,.fr-view textarea.fr-rounded{border-radius:10px;-moz-border-radius:10px;-webkit-border-radius:10px;-moz-background-clip:padding;-webkit-background-clip:padding-box;background-clip:padding-box}.fr-view button.fr-large,.fr-view input.fr-large,.fr-view textarea.fr-large{font-size:24px}a.fr-view.fr-strong{font-weight:700}a.fr-view.fr-green{color:green}img.fr-view{position:relative;max-width:100%}img.fr-view.fr-dib{margin:5px auto;display:block;float:none;vertical-align:top}img.fr-view.fr-dib.fr-fil{margin-left:0}img.fr-view.fr-dib.fr-fir{margin-right:0}img.fr-view.fr-dii{display:inline-block;float:none;vertical-align:bottom;margin-left:5px;margin-right:5px;max-width:calc(100% - (2 * 5px))}img.fr-view.fr-dii.fr-fil{float:left;margin:5px 5px 5px 0;max-width:calc(100% - 5px)}img.fr-view.fr-dii.fr-fir{float:right;margin:5px 0 5px 5px;max-width:calc(100% - 5px)}img.fr-view.fr-rounded{border-radius:100%;-moz-border-radius:100%;-webkit-border-radius:100%;-moz-background-clip:padding;-webkit-background-clip:padding-box;background-clip:padding-box}img.fr-view.fr-bordered{border:solid 10px #CCC;-webkit-box-sizing:content-box;-moz-box-sizing:content-box;box-sizing:content-box} </style>
				 
			
			</head>

			<body style="font-family:Arial Narrow, Arial, sans-serif;">
				<xsl:call-template name="dispProfileContent" />
				<xsl:call-template name="dispSect" />
			</body>
		</html>
	</xsl:template>


	<xsl:template name="dispProfileContent">
		<xsl:choose>
			<xsl:when test="count(MessageDisplay) &gt; 0">
				<xsl:apply-templates select="MessageDisplay">
					<xsl:sort select="@position" data-type="number"></xsl:sort>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:when test="count(Segment) &gt; 0">
				<xsl:apply-templates select="Segment">
					<xsl:sort select="@position" data-type="number"></xsl:sort>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:when test="count(Datatype) &gt; 0">
				<xsl:apply-templates select="Datatype">
					<xsl:sort select="@position" data-type="number"></xsl:sort>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:when test="count(ValueSetDefinition) &gt; 0">
				<xsl:apply-templates select="ValueSetDefinition">
					<xsl:sort select="@position" data-type="number"></xsl:sort>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:when test="count(Constraints) &gt; 0">
				<xsl:apply-templates select="Constraints">
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="MessageDisplay">
		<br></br>
		<h3>
			<xsl:value-of select="@Label" />
			-
			<xsl:value-of select="@Description" />
		</h3>
		<br></br>
		<xsl:value-of select="@Comment" />
		<p>
			<table width="100%" border="1" cellspacing="0" cellpadding="1">
				<col style="width:10%"></col>
				<col style="width:20%"></col>
				<col style="width:20%"></col>
				<col style="width:10%"></col>
				<col style="width:10%"></col>
				<col style="width:30%"></col>
				<thead style="background:#F0F0F0; color:#B21A1C; align:center">
					<tr>
						<th>
							Segment
						</th>
						<th>
							Flavor
						</th>
						<th>
							Element name
						</th>
						<th>
							Cardinality
						</th>
						<th>
							Usage
						</th>
						<th>
							Description/Comments
						</th>
					</tr>
				</thead>
				<tbody>
					<xsl:for-each select="Elt">
						<xsl:call-template name="elt">
							<xsl:with-param name="style"
								select="'background-color:white;text-decoration:normal'">
							</xsl:with-param>
						</xsl:call-template>

					</xsl:for-each>
				</tbody>
			</table>
	<xsl:if test="count(./Constraint) &gt; 0">
			<xsl:choose>
				<xsl:when test="normalize-space($inlineConstraints) = 'false'">
					<xsl:if test="count(./Constraint[@Type='cs']) &gt; 0">
						<p>
							<strong>
								<u>Conformance statements</u>
							</strong>
							<table width="100%" border="1" cellspacing="0" cellpadding="1">
								<xsl:call-template name="csheader"></xsl:call-template>
								<tbody>
									<xsl:for-each select="./Constraint[@Type='cs']">
										<xsl:sort select="@Position" data-type="number"></xsl:sort>
										<xsl:apply-templates select="." mode="standalone"></xsl:apply-templates>
									</xsl:for-each>
								</tbody>
							</table>
						</p>
					</xsl:if>
					<xsl:if test="count(./Constraint[@Type='pre']) &gt; 0">
						<p>
							<strong>
								<u>Conditional predicates</u>
							</strong>
							<table width="100%" border="1" cellspacing="0" cellpadding="1">
								<xsl:call-template name="preheader"></xsl:call-template>
								<tbody>
									<xsl:for-each select="./Constraint[@Type='pre']">
										<xsl:sort select="@Position" data-type="number"></xsl:sort>
										<xsl:apply-templates select="." mode="standalone"></xsl:apply-templates>
									</xsl:for-each>
								</tbody>
							</table>
						</p>
					</xsl:if>
				</xsl:when>
			</xsl:choose>
		</xsl:if>
		</p>
		<xsl:value-of disable-output-escaping="yes"
			select="./Text[@Type='UsageNote']" />
		<br></br>
	</xsl:template>

	<xsl:template name="elt">
		<xsl:param name="style" />
		<tr style="{$style}">
			<td>
				<xsl:value-of select="@Ref" />
			</td>
			<td>
				<xsl:value-of select="@Label" />
			</td>
			<td>
				<xsl:value-of select="@Description" />
			</td>
			<td>
				<xsl:if test="(normalize-space(@Min)!='') and (normalize-space(@Max)!='')">
					<xsl:value-of select="concat('[', @Min, '..', @Max, ']')"></xsl:value-of>
				</xsl:if>
			</td>
			<td>
				<xsl:value-of select="@Usage" />
			</td>
			<td>
				<xsl:value-of select="@Comment" />
			</td>
		</tr>
	</xsl:template>


	<xsl:template match="Segment">
		<h3>
			<xsl:value-of select="@Label" />
			-
			<xsl:value-of select="@Description" />
		</h3>
		<br></br>

		<xsl:value-of select="@Comment"></xsl:value-of>
		<br></br>
		<xsl:if test="count(./Text[@Type='Text1']) &gt; 0">
			<h4>
				Pre-definition:
			</h4>
			<p>
				<xsl:value-of disable-output-escaping="yes"
					select="./Text[@Type='Text1']" />
			</p>
		</xsl:if>
		<h4>Segment Definition</h4>
		<p>
			<table width="100%" border="1" cellspacing="0" cellpadding="1">
				<col style="width:5%"></col>
				<col style="width:15%"></col>
				<col style="width:10%"></col>
				<col style="width:10%"></col>
				<col style="width:10%"></col>
				<col style="width:10%"></col>
				<col style="width:10%"></col>
				<col style="width:30%"></col>
				<thead style="background:#F0F0F0; color:#B21A1C; align:center">
					<tr>
						<th>
							Seq
						</th>
						<th>
							Element name
						</th>
						<th>
							Data type
						</th>
						<th>
							Usage
						</th>
						<th>
							Cardinality
						</th>
						<th>
							Length
						</th>
						<th>
							Value Set
						</th>
						<th>
							Comment
						</th>
					</tr>
				</thead>
				<tbody>
					<xsl:for-each select="Field">
						<xsl:sort select="@Position" data-type="number"></xsl:sort>
						<xsl:call-template name="field">
							<xsl:with-param name="style"
								select="'background-color:white;text-decoration:normal'">
							</xsl:with-param>
						</xsl:call-template>

					</xsl:for-each>
				</tbody>
			</table>
		</p>

		<xsl:choose>
			<xsl:when test="normalize-space($inlineConstraints) = 'false'">
				<xsl:if test="count(Field//Constraint[@Type='cs']) &gt; 0">
					<p>
						<strong>
							<u>Conformance statements</u>
						</strong>
						<table width="100%" border="1" cellspacing="0" cellpadding="1">
							<xsl:call-template name="csheader"></xsl:call-template>
							<tbody>
								<xsl:for-each select="Field/Constraint[@Type='cs']">
									<xsl:sort select="@Position" data-type="number"></xsl:sort>
									<xsl:apply-templates select="." mode="standalone"></xsl:apply-templates>
								</xsl:for-each>
							</tbody>
						</table>
					</p>
				</xsl:if>
				<xsl:if test="count(Field//Constraint[@Type='pre']) &gt; 0">
					<p>
						<strong>
							<u>Conditional predicates</u>
						</strong>
						<table width="100%" border="1" cellspacing="0" cellpadding="1">
							<xsl:call-template name="preheader"></xsl:call-template>
							<tbody>
								<xsl:for-each select="Field/Constraint[@Type='pre']">
									<xsl:sort select="@Position" data-type="number"></xsl:sort>
									<xsl:apply-templates select="." mode="standalone"></xsl:apply-templates>
								</xsl:for-each>
							</tbody>
						</table>
					</p>
				</xsl:if>
			</xsl:when>
		</xsl:choose>
		<!-- <xsl:value-of disable-output-escaping="yes" select="./Text[@Type='Text2']" 
			/> -->

		<xsl:if test="count(./Text[@Type='Text2']) &gt; 0">
			<h4>
				Post-definition:
			</h4>
			<p>
				<xsl:value-of disable-output-escaping="yes"
					select="./Text[@Type='Text2']" />
			</p>
		</xsl:if>
		<xsl:if test="count(./Field/Text[@Type='Text']) &gt; 0">
			<h4>Fields Definition Texts</h4>
			<xsl:for-each select="Field">
				<xsl:sort select="@Position" data-type="number"></xsl:sort>
				<xsl:if test="count(Text) &gt; 0">
					<p>
						<b>
							<xsl:value-of select="concat(../@Name,'-',./@Position,':',./@Name)" />
						</b>
						<xsl:value-of disable-output-escaping="yes"
							select="./Text[@Type='Text']" />
					</p>
				</xsl:if>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="count(coconstraints/table) &gt; 0">
			<p>
				<strong>
					<u>Co-constraints</u>
				</strong>
				<xsl:copy-of select="coconstraints/table" />
			</p>
		</xsl:if>
		<br></br>
	</xsl:template>

	<xsl:template name="field">
		<xsl:param name="style" />
		<tr style="{$style}">
			<td>
				<xsl:value-of select="format-number(@Position, '0')" />
			</td>
			<td>
				<xsl:value-of select="@Name" />
			</td>
			<td>
				<xsl:value-of select="@Datatype" />
			</td>
			<td>
				<xsl:value-of select="@Usage" />
			</td>
			<td>
				<xsl:if test="(normalize-space(@Min)!='') and (normalize-space(@Max)!='')">
					<xsl:value-of select="concat('[', @Min, '..', @Max, ']')"></xsl:value-of>
				</xsl:if>
			</td>
			<td>
				<xsl:if test="(normalize-space(@MinLength)!='') and (normalize-space(@MaxLength)!='')">
					<xsl:value-of select="concat('[', @MinLength, '..', @MaxLength, ']')"></xsl:value-of>
				</xsl:if>
			</td>
			<td>
				<xsl:value-of select="@Binding" />
			</td>
			<td>
				<xsl:value-of select="@Comment" />
			</td>
		</tr>
		<xsl:if test="normalize-space($inlineConstraints) = 'true'">
			<xsl:if test="count(Constraint) &gt; 0">
				<xsl:apply-templates select="." mode="inlineSgt"></xsl:apply-templates>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template match="SingleValueSetBreak">
		<br></br>
		<xsl:value-of select="@Value" />
	</xsl:template>

	<xsl:template match="Datatype">
		<br></br>
		<h3>
			<xsl:value-of select="@Label" />
			-
			<xsl:value-of select="@Description" />
		</h3>

		<xsl:if test="count(Text[@Type='PurposeAndUse']) &gt; 0">
			<p>
				<xsl:value-of disable-output-escaping="yes"
					select="Text[@Type='PurposeAndUse']" />
			</p>
		</xsl:if>
		<xsl:value-of select="@Comment"></xsl:value-of>
		<xsl:if test="count(Text[@Type='UsageNote']) &gt; 0">
			<h4>Usage Note </h4>
			<p>
				<xsl:value-of disable-output-escaping="yes"
					select="Text[@Type='UsageNote']" />
			</p>
		</xsl:if>

		<xsl:if test="count(./Text[@Type='Text1']) &gt; 0">
			<h4>
				pre-definition:
			</h4>
			<p>
				<xsl:value-of disable-output-escaping="yes"
					select="./Text[@Type='Text1']" />
			</p>
		</xsl:if>
		<h4>Datatype Definition</h4>
		<p>
			<table width="100%" border="1" cellspacing="0" cellpadding="0">
				<col style="width:5%"></col>
				<col style="width:15%"></col>
				<col style="width:10%"></col>
				<col style="width:10%"></col>
				<col style="width:10%"></col>
				<col style="width:10%"></col>
				<col style="width:10%"></col>
				<col style="width:30%"></col>
				<thead style="background:#F0F0F0; color:#B21A1C; align:center">
					<tr>
						<th>
							Seq
						</th>
						<th>
							Element name
						</th>
						<th>
							Data type
						</th>
						<th>
							Usage
						</th>
						<th>
							Length
						</th>
						<th>
							Conf length
						</th>
						<th>
							Value set
						</th>
						<th>
							Comment
						</th>
					</tr>
				</thead>
				<tbody>
					<xsl:for-each select="Component">
						<xsl:sort select="@Position" data-type="number"></xsl:sort>
						<xsl:call-template name="component">
							<xsl:with-param name="style"
								select="'background-color:white;text-decoration:normal'">
							</xsl:with-param>
						</xsl:call-template>
					</xsl:for-each>
				</tbody>
			</table>
		</p>

		<xsl:if test="count(./Constraint) &gt; 0">
			<xsl:choose>
				<xsl:when test="normalize-space($inlineConstraints) = 'false'">
					<xsl:if test="count(./Constraint[@Type='cs']) &gt; 0">
						<p>
							<strong>
								<u>Conformance statements</u>
							</strong>
							<table width="100%" border="1" cellspacing="0" cellpadding="1">
								<xsl:call-template name="csheader"></xsl:call-template>
								<tbody>
									<xsl:for-each select="./Constraint[@Type='cs']">
										<xsl:sort select="@Position" data-type="number"></xsl:sort>
										<xsl:apply-templates select="." mode="standalone"></xsl:apply-templates>
									</xsl:for-each>
								</tbody>
							</table>
						</p>
					</xsl:if>
					<xsl:if test="count(./Constraint[@Type='pre']) &gt; 0">
						<p>
							<strong>
								<u>Conditional predicates</u>
							</strong>
							<table width="100%" border="1" cellspacing="0" cellpadding="1">
								<xsl:call-template name="preheader"></xsl:call-template>
								<tbody>
									<xsl:for-each select="./Constraint[@Type='pre']">
										<xsl:sort select="@Position" data-type="number"></xsl:sort>
										<xsl:apply-templates select="." mode="standalone"></xsl:apply-templates>
									</xsl:for-each>
								</tbody>
							</table>
						</p>
					</xsl:if>
				</xsl:when>
			</xsl:choose>
		</xsl:if>
		<xsl:if test="count(./Component/Text[@Type='Text']) &gt; 0">
			<h4>Components Definition Texts</h4>
			<xsl:for-each select="Component">
				<xsl:sort select="@Position" data-type="number"></xsl:sort>
				<xsl:if test="count(./Text[@Type='Text']) &gt; 0">
					<p>
						<strong>
							<xsl:value-of disable-output-escaping="yes" select="concat(../@Name, '-', @Position, ':', @Name)" />
						</strong>
						<xsl:value-of disable-output-escaping="yes"
							select="./Text[@Type='Text']" />
					</p>
				</xsl:if>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="count(./Text[@Type='Text2']) &gt; 0">
			<h4>
				post-definition:
			</h4>
				<xsl:if test="count(./Text[@Type='Text']) &gt; 0">
					<p>
						<u><xsl:value-of select="./Text[@Type='Name']" />: </u>
						<xsl:value-of disable-output-escaping="yes"
							select="./Text[@Type='Text']" />
					</p>
				</xsl:if>
			<p>
				<xsl:value-of disable-output-escaping="yes"
					select="./Text[@Type='Text2']" />
			</p>
		</xsl:if>


	</xsl:template>

	<xsl:template name="component">
		<xsl:param name="style" />
		<tr style="{$style}">

			<td>
				<xsl:value-of select="format-number(@Position, '0')" />
			</td>
			<td>
				<xsl:value-of select="@Name" />
			</td>
			<td>
				<xsl:value-of select="@Datatype" />
			</td>
			<td>
				<xsl:value-of select="@Usage" />
			</td>
			<td>
				<xsl:if test="(normalize-space(@MinLength)!='') and (normalize-space(@MaxLength)!='')">
					<xsl:value-of select="concat('[', @MinLength, '..', @MaxLength, ']')"></xsl:value-of>
				</xsl:if>
			</td>
			<td>
				<xsl:value-of select="@ConfLength" />
			</td>
			<td>
				<xsl:value-of disable-output-escaping="yes" select="@Binding" />
			</td>
			<td>
				<xsl:value-of select="@Comment" />
			</td>
		</tr>

		<xsl:if test="normalize-space($inlineConstraints) = 'true'">
			<xsl:if test="count(Constraint) &gt; 0">
				<xsl:apply-templates select="." mode="inlineDt"></xsl:apply-templates>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template match="ValueSetDefinition">
		<br></br>
		<h3>
			<xsl:value-of select="@BindingIdentifier" />
			-
			<xsl:value-of select="@Description" />
		</h3>
		<xsl:if test="count(./Text[@Type='Text1']) &gt; 0">
			<h4>
				pre-definition:
			</h4>
			<p>
				<xsl:value-of disable-output-escaping="yes"
					select="./Text[@Type='Text1']" />
			</p>
		</xsl:if>


		<xsl:if test="@Stability != ''">
			<p>
				<xsl:text>Stability: </xsl:text>
				<xsl:value-of select="@Stability"></xsl:value-of>
			</p>
		</xsl:if>
		<xsl:if test="@Extensibility != ''">
			<p>
				<xsl:text>Extensibility: </xsl:text>
				<xsl:value-of select="@Extensibility"></xsl:value-of>
			</p>
		</xsl:if>
		<xsl:if test="@ContentDefinition != ''">
			<p>
				<xsl:text>Content Definition: </xsl:text>
				<xsl:value-of select="@ContentDefinition"></xsl:value-of>
			</p>
		</xsl:if>
		<xsl:if test="@Oid != ''">
			<p>
				<xsl:text>Oid: </xsl:text>
				<xsl:value-of select="@Oid"></xsl:value-of>
			</p>
		</xsl:if>
		<h4>Value Set Definition</h4>
		<table width="100%" border="1" cellspacing="0" cellpadding="0">
			<col style="width:15%"></col>
			<col style="width:15%"></col>
			<col style="width:10%"></col>
			<col style="width:60%"></col>
			<thead style="background:#F0F0F0; color:#B21A1C; align:center">
				<tr>
					<th>
						Value
					</th>
					<th>
						Code System
					</th>
					<th>
						Usage
					</th>
					<th>
						Description
					</th>
				</tr>
			</thead>
			<tbody>
				<xsl:for-each select="ValueElement">
					<xsl:sort select="@Value" />
					<xsl:call-template name="ValueElement">
						<xsl:with-param name="style" select="'background-color:white;'">
						</xsl:with-param>
					</xsl:call-template>
				</xsl:for-each>
			</tbody>
		</table>



		<xsl:if test="count(./Text[@Type='Text2']) &gt; 0">
			<h4>
				post-definition:
			</h4>
			<p>
				<xsl:value-of disable-output-escaping="yes"
					select="./Text[@Type='Text2']" />
			</p>
		</xsl:if>
		<!-- <br></br> -->
	</xsl:template>

	<xsl:template name="ValueElement">
		<xsl:param name="style" />
		<tr style="{$style}">
			<td>
				<xsl:value-of select="@Value" />
			</td>
			<td>
				<xsl:value-of select="@CodeSystem" />
			</td>
			<td>
				<xsl:value-of select="@Usage" />
			</td>
			<td>
				<xsl:value-of select="@Label" />
			</td>
		</tr>
	</xsl:template>


	<xsl:template match="Constraints">
		<xsl:if test="count(./Constraint) &gt; 0">
			<b>
				<xsl:value-of select="@title" />
			</b>
			<br></br>
			<p>
				<xsl:if test="./@Type='ConditionPredicate'">
					<table width="100%" border="1" cellspacing="0" cellpadding="0">
						<xsl:call-template name="csheader"></xsl:call-template>
						<tbody>
							<xsl:for-each select="./Constraint">
								<xsl:sort select="@Position" data-type="number"></xsl:sort>
								<xsl:apply-templates select="." mode="standalone"></xsl:apply-templates>
							</xsl:for-each>
						</tbody>
					</table>
					<br></br>
				</xsl:if>
				<xsl:if test="./@Type='ConformanceStatement'">
					<table width="100%" border="1" cellspacing="0" cellpadding="0">
						<xsl:call-template name="preheader"></xsl:call-template>
						<tbody>
							<xsl:for-each select="./Constraint">
								<xsl:sort select="@Position" data-type="number"></xsl:sort>
								<xsl:apply-templates select="." mode="standalone"></xsl:apply-templates>
							</xsl:for-each>
						</tbody>
					</table>
					<br />
				</xsl:if>
			</p>
		</xsl:if>
	</xsl:template>

	<!-- Conformance statement header -->
	<xsl:template name="csheader">
		<col style="width:10%"></col>
		<col style="width:10%"></col>
		<col style="width:80%"></col>
		<thead>
			<tr style="background:#F0F0F0; color:#B21A1C; align:center">
				<th>
					Id
				</th>
				<th>
					Location
				</th>
				<th>
					Description
				</th>
			</tr>
		</thead>

	</xsl:template>

	<!-- Predicate header -->
	<xsl:template name="preheader">
		<col style="width:10%"></col>
		<col style="width:10%"></col>
		<col style="width:80%"></col>
		<thead style="background:#F0F0F0; color:#B21A1C; align:center">
			<tr>
				<th>
					Location
				</th>
				<th>
					Usage
				</th>
				<th>
					Description
				</th>
			</tr>
		</thead>
	</xsl:template>

	<!-- Parse constraint for inline mode segment -->
	<xsl:template match="Constraint" mode="inlineSgt">
		<xsl:variable name="precolspan" select="4"></xsl:variable>
		<xsl:variable name="cscolspan" select="4"></xsl:variable>
		<xsl:if test="./@Type='pre'">
			<tr style="background-color:#E8E8E8;text-decoration:normal">
				<td>
				</td>
				<td>
				</td>
				<td>
				</td>
				<td>
					<xsl:value-of select="@Usage" />
				</td>
				<xsl:element name="td">
					<xsl:attribute name="colspan">
				<xsl:value-of select="$precolspan" />
				</xsl:attribute>
					<xsl:value-of select="." />
				</xsl:element>
			</tr>
		</xsl:if>
		<xsl:if test="./@Type='cs'">
			<tr style="background-color:#E8E8E8;text-decoration:normal">
				<td>
				</td>
				<td>
				</td>
				<td>
				</td>
				<xsl:element name="td">
					<xsl:attribute name="colspan">
				<xsl:value-of select="number($cscolspan)" />	
				</xsl:attribute>
					<xsl:value-of select="@Id" />
					:
					<xsl:value-of select="." />
				</xsl:element>
			</tr>
		</xsl:if>
	</xsl:template>

	<!-- Parse constraint for inline mode datatype -->
	<xsl:template match="Constraint" mode="inlineDt">
		<xsl:variable name="precolspan" select="4"></xsl:variable>
		<xsl:variable name="cscolspan" select="5"></xsl:variable>
		<xsl:if test="./@Type='pre'">
			<tr style="background-color:#E8E8E8;text-decoration:normal">
				<td>
				</td>
				<td>
				</td>
				<td>
				</td>
				<td>
					<xsl:value-of select="@Usage" />
				</td>
				<xsl:element name="td">
					<xsl:attribute name="colspan">
				<xsl:value-of select="$precolspan" />
				</xsl:attribute>
					<xsl:value-of select="." />
				</xsl:element>
			</tr>
		</xsl:if>
		<xsl:if test="./@Type='cs'">
			<tr style="background-color:#E8E8E8;text-decoration:normal">
				<td>
				</td>
				<td>
				</td>
				<td>
				</td>
				<xsl:element name="td">
					<xsl:attribute name="colspan">
				<xsl:value-of select="number($cscolspan)" />	
				</xsl:attribute>
					<xsl:value-of select="@Id" />
					:
					<xsl:value-of select="." />
				</xsl:element>
			</tr>
		</xsl:if>
	</xsl:template>

	<!-- Parse constraint for standalone mode -->
	<xsl:template match="Constraint" mode="standalone">
		<xsl:if test="./@Type='pre'">
			<tr style="background-color:white;text-decoration:normal">
				<td>
					<xsl:value-of select="concat(@LocationName, @Location)" />
				</td>
				<td>
					<xsl:value-of select="@Usage" />
				</td>
				<td>
					<xsl:value-of select="." />
				</td>
			</tr>

		</xsl:if>
		<xsl:if test="./@Type='cs'">
			<tr style="background-color:white;text-decoration:normal">
				<td>
					<xsl:value-of select="@Id" />
				</td>
				<td>
					<xsl:value-of select="concat(@LocationName, @Location)" />
				</td>
				<td>
					<xsl:value-of select="." />
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<xsl:template name="dispSect">
		<xsl:call-template name="dispInfoSect" />
		<xsl:for-each select="*">
			<xsl:sort select="@position" data-type="number"></xsl:sort>
			<xsl:call-template name="dispSect" />
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="dispInfoSect" mode="disp">
		<xsl:if test="name() = 'Section'">
			<a id="{@id}" name="{@id}">
				<u>
					<xsl:choose>
						<xsl:when test="@h &lt; 7">
							<xsl:element name="{concat('h', @h)}">
								<xsl:value-of select="@title" />
							</xsl:element>
						</xsl:when>
						<xsl:when test="@h &gt; 7">
							<xsl:element name="h6">
								<xsl:value-of select="@title" />
							</xsl:element>
						</xsl:when>
					</xsl:choose>
				</u>
			</a>
			<br />
			<xsl:call-template name="dispSectContent" />
		</xsl:if>
	</xsl:template>

	<xsl:template name="dispSectContent">
		<xsl:value-of disable-output-escaping="yes" select="SectionContent" />
	</xsl:template>
</xsl:stylesheet>