package run.halo.app.ext.navidrome;

import org.springframework.stereotype.Component;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import reactor.core.publisher.Mono;
import run.halo.app.plugin.ReactiveSettingFetcher;
import run.halo.app.theme.dialect.TemplateFooterProcessor;

@Component
public class NavidromeFooterProcessor implements TemplateFooterProcessor {

    private static final String PJAX_MODE_PLACEHOLDER = "__HALO_NAVIDROME_PJAX_MODE__";
    private static final String FOOTER_HTML = """
        <!-- navidrome-player start -->
        <style>
          #halo-navidrome-aplayer.aplayer {
            position: relative;
            margin: 0;
            overflow: hidden;
            color: #444;
            background: #fff;
            border-radius: 18px;
            box-shadow: 0 2px 2px 0 rgba(0, 0, 0, 0.07), 0 1px 5px 0 rgba(0, 0, 0, 0.1);
            font-family: Arial, Helvetica, sans-serif;
            line-height: normal;
            user-select: none;
          }
          #halo-navidrome-aplayer.aplayer * {
            box-sizing: content-box;
          }
          #halo-navidrome-aplayer.aplayer svg {
            width: 100%;
            height: 100%;
          }
          #halo-navidrome-aplayer.aplayer svg circle,
          #halo-navidrome-aplayer.aplayer svg path {
            fill: #fff !important;
          }
          #halo-navidrome-aplayer.aplayer.aplayer-withlist .aplayer-info {
            border-bottom: 1px solid #e9e9e9;
          }
          #halo-navidrome-aplayer.aplayer.aplayer-withlist .aplayer-list {
            display: block;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-body {
            position: relative;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-pic {
            position: relative;
            float: left;
            width: 66px;
            height: 66px;
            cursor: pointer;
            background-position: 50%;
            background-size: cover;
            transition: all 0.3s ease;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-pic:hover .aplayer-button {
            opacity: 1;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-pic .aplayer-button {
            position: absolute;
            display: flex;
            align-items: center;
            justify-content: center;
            opacity: 0.8;
            border-radius: 50%;
            text-shadow: 0 1px 1px rgba(0, 0, 0, 0.2);
            box-shadow: 0 1px 1px rgba(0, 0, 0, 0.2);
            background: rgba(0, 0, 0, 0.2);
            transition: all 0.1s ease;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-pic .aplayer-play {
            right: 50%;
            bottom: 50%;
            width: 26px;
            height: 26px;
            margin: 0 -15px -15px 0;
            border: 2px solid #fff;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-pic .aplayer-play svg {
            position: static;
            width: 20px;
            height: 20px;
            transform: translateX(1px);
          }
          #halo-navidrome-aplayer.aplayer .aplayer-pic .aplayer-pause {
            right: 4px;
            bottom: 4px;
            width: 16px;
            height: 16px;
            border: 2px solid #fff;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-pic .aplayer-pause svg {
            position: static;
            width: 10px;
            height: 10px;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-pic .aplayer-hide,
          #halo-navidrome-aplayer.aplayer .aplayer-icon-back,
          #halo-navidrome-aplayer.aplayer .aplayer-icon-forward,
          #halo-navidrome-aplayer.aplayer .aplayer-icon-play,
          #halo-navidrome-aplayer.aplayer .aplayer-icon-lrc {
            display: none;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-info {
            height: 66px;
            margin-left: 66px;
            padding: 14px 7px 0 10px;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-info .aplayer-music {
            height: 20px;
            margin: 0 0 13px 5px;
            overflow: hidden;
            white-space: nowrap;
            text-overflow: ellipsis;
            user-select: text;
            cursor: default;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-info .aplayer-music .aplayer-title {
            font-size: 14px;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-info .aplayer-music .aplayer-author {
            color: #666;
            font-size: 12px;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-info .aplayer-controller {
            position: relative;
            display: flex;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-info .aplayer-controller .aplayer-bar-wrap {
            flex: 1;
            margin: 0 0 0 5px;
            padding: 4px 0;
            cursor: pointer !important;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-info .aplayer-controller .aplayer-bar-wrap .aplayer-bar {
            position: relative;
            width: 100%;
            height: 2px;
            background: #cdcdcd;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-info .aplayer-controller .aplayer-bar-wrap .aplayer-bar .aplayer-loaded {
            position: absolute;
            top: 0;
            left: 0;
            bottom: 0;
            height: 2px;
            background: #aaa;
            transition: all 0.5s ease;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-info .aplayer-controller .aplayer-bar-wrap .aplayer-bar .aplayer-played {
            position: absolute;
            top: 0;
            left: 0;
            bottom: 0;
            height: 2px;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-info .aplayer-controller .aplayer-bar-wrap .aplayer-bar .aplayer-played .aplayer-thumb {
            position: absolute;
            top: 0;
            right: 5px;
            width: 10px;
            height: 10px;
            margin-top: -4px;
            margin-right: -10px;
            border-radius: 50%;
            cursor: pointer;
            transform: scale(0);
            transition: all 0.3s ease-in-out;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-info .aplayer-controller .aplayer-bar-wrap:hover .aplayer-bar .aplayer-played .aplayer-thumb {
            transform: scale(1);
          }
          #halo-navidrome-aplayer.aplayer .aplayer-info .aplayer-controller .aplayer-time {
            position: relative;
            right: 0;
            bottom: 4px;
            height: 17px;
            padding-left: 7px;
            color: #999;
            font-size: 11px;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-info .aplayer-controller .aplayer-time .aplayer-time-inner {
            vertical-align: middle;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-icon {
            display: inline-block;
            width: 15px;
            height: 15px;
            margin: 0;
            padding: 0;
            border: none;
            outline: none;
            opacity: 0.8;
            cursor: pointer;
            vertical-align: middle;
            background: transparent;
            font-size: 12px;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-info .aplayer-controller .aplayer-time .aplayer-icon path {
            fill: #666 !important;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-info .aplayer-controller .aplayer-time .aplayer-icon:hover path {
            fill: #000 !important;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-info .aplayer-controller .aplayer-volume-wrap {
            position: relative;
            display: inline-block;
            margin-left: 3px;
            cursor: pointer !important;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-info .aplayer-controller .aplayer-volume-wrap:hover .aplayer-volume-bar-wrap,
          #halo-navidrome-aplayer.aplayer .aplayer-info .aplayer-controller .aplayer-volume-wrap .aplayer-volume-bar-wrap.aplayer-volume-bar-wrap-active {
            height: 40px;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-info .aplayer-controller .aplayer-volume-wrap .aplayer-volume-bar-wrap {
            position: absolute;
            right: -3px;
            bottom: 15px;
            z-index: 99;
            width: 25px;
            height: 0;
            overflow: hidden;
            transition: all 0.2s ease-in-out;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-info .aplayer-controller .aplayer-volume-wrap .aplayer-volume-bar-wrap .aplayer-volume-bar {
            position: absolute;
            right: 10px;
            bottom: 0;
            width: 5px;
            height: 35px;
            overflow: hidden;
            background: #aaa;
            border-radius: 2.5px;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-info .aplayer-controller .aplayer-volume-wrap .aplayer-volume-bar-wrap .aplayer-volume-bar .aplayer-volume {
            position: absolute;
            right: 0;
            bottom: 0;
            width: 5px;
            transition: all 0.1s ease;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-list {
            display: none;
            overflow: hidden;
            transition: all 0.5s ease;
            will-change: height;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-list ol {
            margin: 0;
            padding: 0;
            overflow-y: auto;
            list-style-type: none;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-list ol li {
            position: relative;
            height: 32px;
            margin: 0;
            padding: 0 15px;
            overflow: hidden;
            color: #444;
            cursor: pointer;
            border-top: 1px solid #e9e9e9;
            font-size: 12px;
            line-height: 32px;
            transition: all 0.2s ease;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-list ol li:first-child {
            border-top: none;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-list ol li:hover {
            background: #efefef;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-list ol li.aplayer-list-light {
            background: #e9e9e9;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-list ol li .aplayer-list-cur {
            display: none;
            position: absolute;
            top: 5px;
            left: 0;
            width: 3px;
            height: 22px;
            cursor: pointer;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-list ol li.aplayer-list-light .aplayer-list-cur {
            display: inline-block;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-list ol li .aplayer-list-index,
          #halo-navidrome-aplayer.aplayer .aplayer-list ol li .aplayer-list-author {
            color: #666;
            cursor: pointer;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-list ol li .aplayer-list-index {
            margin-right: 12px;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-list ol li .aplayer-list-author {
            float: right;
          }
          #halo-navidrome-root {
            position: fixed;
            left: 24px;
            top: calc(100vh - 96px);
            z-index: 9999;
            width: 60px;
            height: 60px;
            user-select: none;
          }
          #halo-navidrome-root * {
            box-sizing: border-box;
          }
          #halo-navidrome-bubble {
            position: relative;
            isolation: isolate;
            display: flex;
            align-items: center;
            justify-content: center;
            width: 60px;
            height: 60px;
            border-radius: 16px;
            border: 0;
            cursor: grab;
            color: #fff;
            font-size: 22px;
            font-weight: 700;
            background: linear-gradient(135deg, #1f9d8b, #2b7de9);
            box-shadow: 0 18px 40px rgba(8, 15, 52, 0.22);
          }
          #halo-navidrome-bubble::before,
          #halo-navidrome-bubble::after {
            content: '';
            position: absolute;
            inset: -5px;
            z-index: -1;
            border-radius: 20px;
            border: 2px solid rgba(43, 125, 233, 0.28);
            opacity: 0;
            transform: scale(0.92);
            pointer-events: none;
          }
          #halo-navidrome-root.is-playing #halo-navidrome-bubble::before {
            animation: haloNavidromePulse 1.8s ease-out infinite;
          }
          #halo-navidrome-root.is-playing #halo-navidrome-bubble::after {
            animation: haloNavidromePulse 1.8s ease-out 0.9s infinite;
          }
          #halo-navidrome-root.is-dragging #halo-navidrome-bubble {
            cursor: grabbing;
          }
          @keyframes haloNavidromePulse {
            0% {
              opacity: 0.48;
              transform: scale(1);
            }
            100% {
              opacity: 0;
              transform: scale(1.42);
            }
          }
          #halo-navidrome-panel {
            position: absolute;
            left: 0;
            bottom: 72px;
            width: min(420px, calc(100vw - 32px));
            max-width: min(420px, calc(100vw - 32px));
            opacity: 0;
            pointer-events: none;
            transform: translateY(12px) scale(0.98);
            transform-origin: bottom left;
            transition: opacity 0.18s ease, transform 0.18s ease;
          }
          #halo-navidrome-root.is-panel-left #halo-navidrome-panel {
            left: auto;
            right: 0;
            transform-origin: bottom right;
          }
          #halo-navidrome-root.is-open #halo-navidrome-panel {
            opacity: 1;
            pointer-events: auto;
            transform: translateY(0) scale(1);
          }
          #halo-navidrome-playlists-drawer {
            position: absolute;
            left: calc(100% + 12px);
            bottom: 72px;
            width: 220px;
            max-width: min(220px, calc(100vw - 32px));
            opacity: 0;
            pointer-events: none;
            transform: translateX(-8px) scale(0.98);
            transform-origin: bottom left;
            transition: opacity 0.18s ease, transform 0.18s ease;
          }
          #halo-navidrome-root.is-panel-left #halo-navidrome-playlists-drawer {
            left: auto;
            right: calc(100% + 12px);
            transform-origin: bottom right;
          }
          #halo-navidrome-root.is-playlists-open #halo-navidrome-playlists-drawer {
            opacity: 1;
            pointer-events: auto;
            transform: translateX(0) scale(1);
          }
          #halo-navidrome-playlists-drawer.is-hidden {
            display: none;
          }
          #halo-navidrome-playlists-card {
            overflow: hidden;
            border-radius: 18px;
            background: #fff;
            box-shadow: 0 18px 44px rgba(8, 15, 52, 0.16);
          }
          #halo-navidrome-playlists-title {
            padding: 12px 14px 10px;
            border-bottom: 1px solid #eef2f7;
            color: #0f172a;
            font-size: 13px;
            font-weight: 700;
            line-height: 1.4;
          }
          #halo-navidrome-playlists-list {
            max-height: 250px;
            overflow-y: auto;
          }
          #halo-navidrome-playlists-list::-webkit-scrollbar {
            width: 5px;
          }
          #halo-navidrome-playlists-list::-webkit-scrollbar-thumb {
            border-radius: 999px;
            background: #d7e4f5;
          }
          .halo-navidrome-playlist-option {
            display: flex;
            align-items: center;
            gap: 10px;
            width: 100%;
            padding: 10px 12px;
            border: 0;
            border-top: 1px solid #eef2f7;
            color: #334155;
            text-align: left;
            background: #fff;
            cursor: pointer;
          }
          .halo-navidrome-playlist-option:hover {
            background: #eef7ff;
          }
          .halo-navidrome-playlist-option.is-active {
            background: #dcefff;
          }
          .halo-navidrome-playlist-option-index {
            flex: 0 0 18px;
            color: #64748b;
            font-size: 12px;
            text-align: center;
          }
          .halo-navidrome-playlist-option-main {
            min-width: 0;
            flex: 1 1 auto;
          }
          .halo-navidrome-playlist-option-name {
            display: block;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            font-size: 13px;
            line-height: 1.4;
          }
          .halo-navidrome-playlist-option-meta {
            display: block;
            margin-top: 2px;
            color: #94a3b8;
            font-size: 11px;
            line-height: 1.3;
          }
          #halo-navidrome-list-pager {
            display: grid;
            grid-template-columns: auto minmax(0, 1fr) auto;
            align-items: center;
            gap: 8px;
            width: 100%;
            padding: 8px 10px 10px;
            margin: 0;
            background: #fff;
            border-top: 1px solid #eef2f7;
            box-sizing: border-box !important;
          }
          #halo-navidrome-list-pager,
          #halo-navidrome-list-pager * {
            box-sizing: border-box !important;
          }
          #halo-navidrome-list-pager.is-hidden {
            display: none;
          }
          #halo-navidrome-list-pager button {
            position: relative;
            width: 18px;
            min-width: 18px;
            height: 20px;
            padding: 0;
            border: 0;
            color: transparent;
            background: transparent;
            font-size: 0;
            line-height: 0;
            cursor: pointer;
          }
          #halo-navidrome-list-pager button::before {
            content: '';
            position: absolute;
            inset: 0;
            display: block;
            margin: auto;
            width: 14px;
            height: 20px;
            background: linear-gradient(135deg, #7fc4ee, #1f6fd8);
            filter: drop-shadow(0 1px 2px rgba(24, 80, 171, 0.58));
          }
          #halo-navidrome-page-prev::before {
            clip-path: polygon(100% 0, 0 50%, 100% 100%);
          }
          #halo-navidrome-page-next::before {
            clip-path: polygon(0 0, 100% 50%, 0 100%);
          }
          #halo-navidrome-page-prev {
            justify-self: start;
          }
          #halo-navidrome-page-next {
            justify-self: end;
          }
          #halo-navidrome-list-pager button:disabled {
            opacity: 0.35;
            cursor: not-allowed;
          }
          #halo-navidrome-list-page-text {
            min-width: 0;
            text-align: center;
            color: #64748b;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            font-size: 12px;
            line-height: 1.4;
          }
          #halo-navidrome-status {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 12px;
            padding: 14px 16px;
            margin-bottom: 12px;
            border-radius: 18px;
            color: #0f172a;
            background: rgba(255, 255, 255, 0.98);
            box-shadow: 0 18px 44px rgba(8, 15, 52, 0.12);
          }
          #halo-navidrome-status.is-hidden {
            display: none;
          }
          #halo-navidrome-status.is-loading {
            border: 1px solid rgba(43, 125, 233, 0.18);
          }
          #halo-navidrome-status.is-error {
            border: 1px solid rgba(220, 38, 38, 0.18);
            color: #991b1b;
          }
          #halo-navidrome-status-text {
            flex: 1;
            font-size: 14px;
            line-height: 1.5;
          }
          #halo-navidrome-status-action {
            display: none;
            align-items: center;
            justify-content: center;
            min-width: 72px;
            padding: 8px 12px;
            border: 0;
            border-radius: 999px;
            cursor: pointer;
            color: #fff;
            background: #1f9d8b;
          }
          #halo-navidrome-status.is-actionable #halo-navidrome-status-action {
            display: inline-flex;
          }
          #halo-navidrome-aplayer {
            overflow: hidden;
            border-radius: 18px;
            box-shadow: 0 18px 44px rgba(8, 15, 52, 0.18);
          }
          #halo-navidrome-aplayer.aplayer {
            margin: 0;
            border-radius: 18px;
            font-size: 14px !important;
            line-height: 1.5 !important;
          }
          #halo-navidrome-aplayer.aplayer * {
            max-width: none;
          }
          #halo-navidrome-aplayer.aplayer button,
          #halo-navidrome-aplayer.aplayer input,
          #halo-navidrome-aplayer.aplayer svg {
            min-width: 0 !important;
            min-height: 0 !important;
            max-width: none !important;
            max-height: none !important;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-body {
            display: flex !important;
            align-items: stretch !important;
            min-height: 112px !important;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-pic {
            float: none !important;
            flex: 0 0 112px !important;
            width: 112px !important;
            height: 112px !important;
            min-width: 112px !important;
            background-size: contain !important;
            background-repeat: no-repeat !important;
            background-position: center center !important;
            background-color: #f5f7fb !important;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-button {
            display: none !important;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-pic .aplayer-play {
            right: 50% !important;
            bottom: 50% !important;
            width: 42px !important;
            height: 42px !important;
            margin: 0 !important;
            transform: translate(50%, 50%) !important;
            border: 2px solid #fff !important;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-pic .aplayer-pause {
            right: 10px !important;
            bottom: 10px !important;
            width: 24px !important;
            height: 24px !important;
            border: 2px solid #fff !important;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-button svg,
          #halo-navidrome-aplayer.aplayer .aplayer-icon svg {
            width: 18px !important;
            height: 18px !important;
            display: block !important;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-pic .aplayer-play svg,
          #halo-navidrome-aplayer.aplayer .aplayer-pic .aplayer-pause svg {
            display: none !important;
          }
          #halo-navidrome-aplayer .halo-navidrome-cover-icon {
            position: relative;
            display: block;
            width: 18px;
            height: 18px;
            flex: 0 0 18px;
          }
          #halo-navidrome-aplayer .halo-navidrome-cover-icon::before,
          #halo-navidrome-aplayer .halo-navidrome-cover-icon::after {
            content: '';
            position: absolute;
            display: block;
          }
          #halo-navidrome-aplayer .halo-navidrome-cover-icon--play::before {
            left: 4px;
            top: 1px;
            width: 0;
            height: 0;
            border-top: 8px solid transparent;
            border-bottom: 8px solid transparent;
            border-left: 12px solid #fff;
          }
          #halo-navidrome-aplayer .halo-navidrome-cover-icon--pause::before,
          #halo-navidrome-aplayer .halo-navidrome-cover-icon--pause::after {
            top: 1px;
            width: 4px;
            height: 16px;
            border-radius: 999px;
            background: #fff;
          }
          #halo-navidrome-aplayer .halo-navidrome-cover-icon--pause::before {
            left: 4px;
          }
          #halo-navidrome-aplayer .halo-navidrome-cover-icon--pause::after {
            left: 10px;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-icon {
            width: 20px !important;
            height: 20px !important;
            padding: 0 !important;
            margin: 0 4px 0 0 !important;
            display: inline-flex !important;
            align-items: center !important;
            justify-content: center !important;
            flex: 0 0 20px !important;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-icon-lrc {
            display: none !important;
          }
          #halo-navidrome-aplayer.aplayer.aplayer-withlist .aplayer-info .aplayer-controller .aplayer-time .aplayer-icon.aplayer-icon-menu {
            display: inline-flex !important;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-info {
            display: flex !important;
            flex: 1 1 auto !important;
            flex-direction: column !important;
            justify-content: space-between !important;
            min-width: 0 !important;
            margin-left: 0 !important;
            padding: 11px 12px 10px !important;
            height: 112px !important;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-info .aplayer-music {
            display: flex !important;
            flex-direction: column !important;
            justify-content: flex-start !important;
            height: auto !important;
            min-height: 30px !important;
            margin: 0 0 8px !important;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-info .aplayer-music .aplayer-title {
            display: block !important;
            overflow: hidden !important;
            text-overflow: ellipsis !important;
            white-space: nowrap !important;
            font-size: 15px !important;
            line-height: 1.35 !important;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-info .aplayer-music .aplayer-author {
            display: block !important;
            margin-top: 2px !important;
            overflow: hidden !important;
            text-overflow: ellipsis !important;
            white-space: nowrap !important;
            font-size: 11px !important;
            line-height: 1.35 !important;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-controller {
            display: flex !important;
            align-items: center !important;
            gap: 6px !important;
            min-width: 0 !important;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-controller .aplayer-bar-wrap {
            flex: 1 1 auto !important;
            min-width: 0 !important;
            margin-left: 0 !important;
            padding-top: 3px !important;
            padding-bottom: 3px !important;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-time {
            display: flex !important;
            align-items: center !important;
            flex: 0 0 auto !important;
            flex-wrap: nowrap !important;
            gap: 5px !important;
            height: auto !important;
            padding-top: 0 !important;
            padding-left: 0 !important;
            white-space: nowrap !important;
            font-size: 11px !important;
            line-height: 1.3 !important;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-time-inner {
            margin-top: 0 !important;
            display: inline-flex !important;
            align-items: center !important;
            gap: 2px !important;
            white-space: nowrap !important;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-volume-wrap,
          #halo-navidrome-aplayer.aplayer .aplayer-volume-bar-wrap,
          #halo-navidrome-aplayer.aplayer .aplayer-volume-bar {
            box-sizing: border-box !important;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-volume-wrap {
            margin-left: 0 !important;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-list {
            max-height: none !important;
            overflow-y: hidden !important;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-list ol li {
            height: 34px !important;
            padding: 0 12px !important;
            font-size: 12px !important;
            line-height: 34px !important;
            background: #fff !important;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-list ol li:hover {
            background: #e8f5ff !important;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-list ol li.aplayer-list-light {
            background: #d3e8ff !important;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-list ol li .aplayer-list-index {
            margin-right: 8px !important;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-list ol li .aplayer-list-author {
            font-size: 12px !important;
          }
          #halo-navidrome-aplayer.aplayer .aplayer-list ol li.is-hidden-by-page {
            display: none !important;
          }
          #halo-navidrome-pjax-progress {
            position: fixed;
            left: 0;
            top: 0;
            z-index: 10000;
            width: 100%;
            height: 3px;
            opacity: 0;
            pointer-events: none;
            background: linear-gradient(90deg, #1f9d8b, #2b7de9);
            transform: scaleX(0);
            transform-origin: left center;
            transition: opacity 0.2s ease, transform 0.2s ease;
          }
          #halo-navidrome-pjax-progress.is-active {
            opacity: 1;
            transform: scaleX(0.75);
          }
          #halo-navidrome-pjax-progress.is-done {
            opacity: 0;
            transform: scaleX(1);
          }
          body.halo-navidrome-pjax-loading {
            cursor: progress;
          }
          @media (max-width: 640px) {
            #halo-navidrome-root {
              left: 12px;
              top: calc(100vh - 84px);
            }
            #halo-navidrome-panel {
              width: min(340px, calc(100vw - 24px));
              max-width: min(340px, calc(100vw - 24px));
            }
            #halo-navidrome-playlists-drawer {
              width: 180px;
              max-width: min(180px, calc(100vw - 24px));
            }
            #halo-navidrome-aplayer.aplayer .aplayer-body {
              min-height: 96px !important;
            }
            #halo-navidrome-aplayer.aplayer .aplayer-pic {
              flex-basis: 96px !important;
              width: 96px !important;
              height: 96px !important;
              min-width: 96px !important;
            }
            #halo-navidrome-aplayer.aplayer .aplayer-info {
              height: 96px !important;
              padding: 9px 10px 8px !important;
            }
            #halo-navidrome-aplayer.aplayer .aplayer-info .aplayer-music .aplayer-title {
              font-size: 14px !important;
            }
            #halo-navidrome-aplayer.aplayer .aplayer-info .aplayer-music .aplayer-author {
              font-size: 10px !important;
            }
            #halo-navidrome-aplayer.aplayer .aplayer-time {
              font-size: 10px !important;
              gap: 4px !important;
            }
            #halo-navidrome-aplayer.aplayer .aplayer-icon {
              width: 18px !important;
              height: 18px !important;
              flex-basis: 18px !important;
            }
          }
        </style>
        """ + footerHtmlPart2();

    private static String footerHtmlPart2() {
        return """
        <script>
          (function () {
            var state = window.__haloNavidromePlayerState = window.__haloNavidromePlayerState || {};
            var injectedPjaxMode = '__HALO_NAVIDROME_PJAX_MODE__';
            if (state.bootstrapped) {
              state.pjaxMode = injectedPjaxMode === 'plugin' || injectedPjaxMode === 'theme-compatible'
                ? injectedPjaxMode
                : 'off';
              if (typeof state.activatePjaxMode === 'function') {
                state.activatePjaxMode(state.pjaxMode);
              }
              if (typeof state.ensureMounted === 'function') {
                state.ensureMounted();
              }
              return;
            }
            state.bootstrapped = true;

            var STORAGE_KEY = 'halo-navidrome-floating-state';
            var APLAYER_STORAGE_KEY = 'halo-navidrome-aplayer-setting';
            var PJAX_EVENT_PREFIX = 'halo-navidrome:pjax';
            var PLAYBACK_SAVE_INTERVAL = 1000;
            var PLAYLIST_PAGE_SIZE = 5;
            var ASSET_LOAD_TIMEOUT = 12000;
            var QUICK_TAP_THRESHOLD_MS = 220;
            var COVER_PLAY_ICON = '<span class="halo-navidrome-cover-icon halo-navidrome-cover-icon--play" aria-hidden="true"></span>';
            var COVER_PAUSE_ICON = '<span class="halo-navidrome-cover-icon halo-navidrome-cover-icon--pause" aria-hidden="true"></span>';
            var APLAYER_CSS_URLS = [
              'https://cdn.jsdelivr.net/npm/aplayer@1.10.1/dist/APlayer.min.css',
              'https://unpkg.com/aplayer@1.10.1/dist/APlayer.min.css'
            ];
            var APLAYER_SCRIPT_URLS = [
              'https://cdn.jsdelivr.net/npm/aplayer@1.10.1/dist/APlayer.min.js',
              'https://unpkg.com/aplayer@1.10.1/dist/APlayer.min.js'
            ];
            var CONTAINER_SELECTORS = [
              '[data-halo-main]',
              'main',
              '[role="main"]',
              '#content',
              '#main',
              '#primary',
              '.main-content',
              '.site-main',
              '.content-area',
              '.main'
            ];

            function resolvePjaxMode(mode) {
              if (mode === 'plugin' || mode === 'theme-compatible') {
                return mode;
              }
              return 'off';
            }

            state.pjaxMode = resolvePjaxMode(injectedPjaxMode);

            function readStoredState() {
              try {
                var raw = window.localStorage.getItem(STORAGE_KEY);
                return raw ? JSON.parse(raw) : {};
              } catch (error) {
                return {};
              }
            }

            function writeStoredState(payload) {
              try {
                window.localStorage.setItem(STORAGE_KEY, JSON.stringify(payload));
              } catch (error) {
                console.warn('[navidrome-player] Failed to persist state.', error);
              }
            }

            function patchStoredState(partial) {
              state.stored = Object.assign({}, state.stored || readStoredState(), partial);
              writeStoredState(state.stored);
            }

            function getStoredPlaylistPages() {
              var stored = state.stored || readStoredState();
              if (!stored || !stored.playlistPages || Array.isArray(stored.playlistPages)
                || typeof stored.playlistPages !== 'object') {
                return {};
              }
              return stored.playlistPages;
            }

            function getStoredPlaybackStates() {
              var stored = state.stored || readStoredState();
              if (!stored || !stored.playbackStates || Array.isArray(stored.playbackStates)
                || typeof stored.playbackStates !== 'object') {
                return {};
              }
              return stored.playbackStates;
            }

            function getStoredPlaybackState(playlistId) {
              if (!playlistId) {
                return null;
              }
              var playbackStates = getStoredPlaybackStates();
              var playbackState = playbackStates[playlistId];
              if (!playbackState || Array.isArray(playbackState) || typeof playbackState !== 'object') {
                return null;
              }
              return playbackState;
            }

            function patchStoredPlaybackState(playlistId, partial) {
              if (!playlistId || !partial || typeof partial !== 'object') {
                return;
              }
              var playbackStates = Object.assign({}, getStoredPlaybackStates());
              var currentPlaybackState = getStoredPlaybackState(playlistId) || {};
              playbackStates[playlistId] = Object.assign({}, currentPlaybackState, partial);
              patchStoredState({
                playlistId: playlistId,
                playbackStates: playbackStates
              });
            }

            function getRememberedPlaylistPage(playlistId) {
              if (!playlistId) {
                return 1;
              }
              var playlistPages = getStoredPlaylistPages();
              var page = Number(playlistPages[playlistId]);
              if (!Number.isFinite(page) || page < 1) {
                return 1;
              }
              return Math.floor(page);
            }

            function persistCurrentPlaylistPage() {
              if (!state.currentPlaylistId || !Number.isFinite(state.playlistPage) || state.playlistPage < 1) {
                return;
              }
              var playlistPages = Object.assign({}, getStoredPlaylistPages());
              var page = Math.floor(state.playlistPage);
              if (playlistPages[state.currentPlaylistId] === page) {
                return;
              }
              playlistPages[state.currentPlaylistId] = page;
              patchStoredState({
                playlistPages: playlistPages
              });
            }

            function persistUiState() {
              if (!state.position) {
                return;
              }
              var payload = {
                left: state.position.left,
                top: state.position.top,
                open: !!state.open,
                drawerOpen: !!state.drawerOpen
              };
              if (state.currentPlaylistId) {
                payload.playlistId = state.currentPlaylistId;
              }
              patchStoredState(payload);
            }

            function persistPlaybackState() {
              if (!state.player || !state.player.audio) {
                return;
              }
              var volume = state.player.volume();
              patchStoredState({
                volume: Number.isFinite(volume) ? Math.round(volume * 1000) / 1000 : 0.7,
                playlistId: state.currentPlaylistId || ''
              });
              patchStoredPlaybackState(state.currentPlaylistId, {
                trackIndex: state.player.list ? state.player.list.index : 0,
                currentTime: Math.floor(state.player.audio.currentTime || 0),
                paused: !!state.player.audio.paused
              });
            }

            function schedulePersistPlayback() {
              if (state.persistPlaybackTimer) {
                return;
              }
              state.persistPlaybackTimer = window.setTimeout(function () {
                state.persistPlaybackTimer = null;
                persistPlaybackState();
              }, PLAYBACK_SAVE_INTERVAL);
            }

            function persistAllState() {
              persistUiState();
              persistPlaybackState();
            }

            function formatAPlayerTime(totalSeconds) {
              var seconds = Number(totalSeconds);
              if (!Number.isFinite(seconds) || seconds < 0) {
                seconds = 0;
              }
              seconds = Math.floor(seconds);
              var hours = Math.floor(seconds / 3600);
              var minutes = Math.floor((seconds % 3600) / 60);
              var remainSeconds = seconds % 60;
              function pad(value) {
                return value < 10 ? '0' + value : '' + value;
              }
              if (hours > 0) {
                return pad(hours) + ':' + pad(minutes) + ':' + pad(remainSeconds);
              }
              return pad(minutes) + ':' + pad(remainSeconds);
            }

            function syncDisplayedTrackDuration(player) {
              if (!player || !player.template || !player.template.dtime || !player.list || !player.list.audios) {
                return;
              }
              var currentAudio = player.list.audios[player.list.index || 0];
              if (!currentAudio || !Number.isFinite(Number(currentAudio.duration)) || Number(currentAudio.duration) <= 0) {
                return;
              }
              player.template.dtime.textContent = formatAPlayerTime(Number(currentAudio.duration));
            }

            function applyStoredPlaybackPreview(player) {
              if (state.player !== player || !state.pendingRestore) {
                return;
              }
              syncDisplayedTrackDuration(player);
              if (player.template && player.template.ptime) {
                player.template.ptime.textContent = formatAPlayerTime(state.pendingRestore.currentTime || 0);
              }
              if ((state.pendingRestore.currentTime || 0) > 0) {
                previewSeekPosition(player, state.pendingRestore.currentTime, getCurrentTrackDuration(player));
              } else if (player.bar) {
                player.bar.set('played', 0, 'width');
              }
            }

            function scheduleStoredPlaybackPreview(player) {
              window.setTimeout(function () {
                applyStoredPlaybackPreview(player);
              }, 0);
            }

            function getCurrentTrackDuration(player) {
              if (!player) {
                return 0;
              }
              if (Number.isFinite(player.duration) && player.duration > 0) {
                return Number(player.duration);
              }
              if (!player.list || !player.list.audios) {
                return 0;
              }
              var currentAudio = player.list.audios[player.list.index || 0];
              if (!currentAudio || !Number.isFinite(Number(currentAudio.duration)) || Number(currentAudio.duration) <= 0) {
                return 0;
              }
              return Number(currentAudio.duration);
            }

            function previewSeekPosition(player, targetTime, totalDuration) {
              if (!player || !player.template || !Number.isFinite(totalDuration) || totalDuration <= 0) {
                return;
              }
              var safeTime = Math.min(Math.max(Number(targetTime) || 0, 0), totalDuration);
              if (player.template.ptime) {
                player.template.ptime.textContent = formatAPlayerTime(safeTime);
              }
              if (player.bar) {
                player.bar.set('played', safeTime / totalDuration, 'width');
              }
            }

            function rememberPendingSeek(player, targetTime) {
              if (!player || !player.list) {
                return;
              }
              var totalDuration = getCurrentTrackDuration(player);
              if (!Number.isFinite(totalDuration) || totalDuration <= 0) {
                return;
              }
              var safeTime = Math.min(Math.max(Number(targetTime) || 0, 0), totalDuration);
              state.pendingSeek = {
                playlistId: state.currentPlaylistId || '',
                index: player.list.index || 0,
                currentTime: safeTime
              };
              previewSeekPosition(player, safeTime, totalDuration);
              patchStoredPlaybackState(state.currentPlaylistId, {
                trackIndex: player.list.index || 0,
                currentTime: Math.floor(safeTime)
              });
            }

            function applyPendingSeek(player) {
              if (!player || state.player !== player || !player.audio || !player.list) {
                return false;
              }
              var pendingSeek = state.pendingSeek;
              if (!pendingSeek) {
                return false;
              }
              if (pendingSeek.playlistId !== (state.currentPlaylistId || '')
                || pendingSeek.index !== (player.list.index || 0)) {
                state.pendingSeek = null;
                return false;
              }
              if (player.audio.readyState < 1) {
                return false;
              }
              player.seek(pendingSeek.currentTime);
              previewSeekPosition(player, pendingSeek.currentTime, getCurrentTrackDuration(player));
              state.pendingSeek = null;
              window.setTimeout(function () {
                persistPlaybackState();
              }, 0);
              return true;
            }

            function bindCustomProgressBar(player) {
              if (!player || !player.template || !player.template.barWrap) {
                return;
              }
              var barWrap = player.template.barWrap;
              if (barWrap.dataset.haloNavidromeSeekBound === 'true') {
                return;
              }
              barWrap.dataset.haloNavidromeSeekBound = 'true';

              function resolveClientX(event) {
                if (event.changedTouches && event.changedTouches.length) {
                  return event.changedTouches[0].clientX;
                }
                if (event.touches && event.touches.length) {
                  return event.touches[0].clientX;
                }
                return event.clientX;
              }

              function buildPreview(event) {
                var totalDuration = getCurrentTrackDuration(player);
                if (!Number.isFinite(totalDuration) || totalDuration <= 0) {
                  return null;
                }
                if (Number.isFinite(player.duration) && player.duration > 0) {
                  return null;
                }
                var clientX = resolveClientX(event);
                if (!Number.isFinite(clientX)) {
                  return null;
                }
                var rect = barWrap.getBoundingClientRect();
                if (!rect.width) {
                  return null;
                }
                var ratio = Math.min(Math.max((clientX - rect.left) / rect.width, 0), 1);
                return {
                  duration: totalDuration,
                  targetTime: ratio * totalDuration
                };
              }

              function applyPreview(event) {
                var preview = buildPreview(event);
                if (!preview) {
                  return false;
                }
                rememberPendingSeek(player, preview.targetTime);
                return true;
              }

              function onPointerUp(event) {
                applyPreview(event);
                document.removeEventListener('mousemove', onPointerMove);
                document.removeEventListener('mouseup', onPointerUp);
                document.removeEventListener('touchmove', onPointerMove);
                document.removeEventListener('touchend', onPointerUp);
              }

              function onPointerMove(event) {
                applyPreview(event);
              }

              barWrap.addEventListener('mousedown', function (event) {
                if (!applyPreview(event)) {
                  return;
                }
                document.addEventListener('mousemove', onPointerMove);
                document.addEventListener('mouseup', onPointerUp);
              });

              barWrap.addEventListener('touchstart', function (event) {
                if (!applyPreview(event)) {
                  return;
                }
                document.addEventListener('touchmove', onPointerMove);
                document.addEventListener('touchend', onPointerUp);
              }, { passive: true });
            }

            function clamp(value, min, max) {
              return Math.min(Math.max(value, min), max);
            }

            function getViewportWidth() {
              return Math.max(document.documentElement.clientWidth || 0, window.innerWidth || 0);
            }

            function getViewportHeight() {
              return Math.max(document.documentElement.clientHeight || 0, window.innerHeight || 0);
            }

            function resolveDefaultPosition() {
              var stored = state.stored || readStoredState();
              var width = getViewportWidth();
              var height = getViewportHeight();
              return {
                left: typeof stored.left === 'number' ? stored.left : Math.min(24, width - 84),
                top: typeof stored.top === 'number' ? stored.top : Math.max(12, height - 96),
                open: !!stored.open
              };
            }

            function applyPosition() {
              if (!state.root || !state.position) {
                return;
              }
              var maxLeft = Math.max(12, getViewportWidth() - 72);
              var maxTop = Math.max(12, getViewportHeight() - 72);
              state.position.left = clamp(state.position.left, 12, maxLeft);
              state.position.top = clamp(state.position.top, 12, maxTop);
              state.root.style.left = state.position.left + 'px';
              state.root.style.top = state.position.top + 'px';
              applyPanelLayout();
            }

            function applyPanelLayout() {
              if (!state.panel || !state.root || !state.position) {
                return;
              }
              var viewportWidth = getViewportWidth();
              var isMobile = viewportWidth <= 640;
              var rootWidth = state.root.offsetWidth || 60;
              var drawerGap = 12;
              var desiredPanelWidth = isMobile
                ? Math.min(340, Math.max(300, viewportWidth - 24))
                : Math.min(420, Math.max(360, viewportWidth - 32));
              var availableRight = Math.max(0, viewportWidth - state.position.left - 12);
              var availableLeft = Math.max(0, state.position.left + rootWidth - 12);
              var showDrawer = !!(state.drawerOpen && state.playlistsDrawer
                && !state.playlistsDrawer.classList.contains('is-hidden'));
              var desiredDrawerWidth = showDrawer
                ? (isMobile ? 180 : 220)
                : 0;
              var totalDesiredWidth = desiredPanelWidth + (showDrawer ? drawerGap + desiredDrawerWidth : 0);
              var openToLeft = availableRight < totalDesiredWidth;
              if (openToLeft && availableLeft <= availableRight) {
                openToLeft = false;
              }
              var totalAvailable = openToLeft ? availableLeft : availableRight;
              var panelWidth = desiredPanelWidth;
              var drawerWidth = desiredDrawerWidth;
              if (showDrawer) {
                var remainingDrawerWidth = totalAvailable - panelWidth - drawerGap;
                if (remainingDrawerWidth < drawerWidth) {
                  drawerWidth = Math.max(150, remainingDrawerWidth);
                }
                if ((panelWidth + drawerGap + drawerWidth) > totalAvailable) {
                  panelWidth = Math.max(300, totalAvailable - drawerGap - drawerWidth);
                }
              } else {
                panelWidth = Math.min(
                  desiredPanelWidth,
                  Math.max(300, totalAvailable)
                );
              }
              state.panel.style.width = panelWidth + 'px';
              state.panel.style.maxWidth = panelWidth + 'px';
              state.panel.style.left = openToLeft ? 'auto' : '0px';
              state.panel.style.right = openToLeft ? '0px' : 'auto';
              state.panel.style.transformOrigin = openToLeft ? 'bottom right' : 'bottom left';
              state.root.classList.toggle('is-panel-left', openToLeft);
              if (state.playlistsDrawer) {
                if (!showDrawer) {
                  drawerWidth = isMobile ? 180 : 220;
                }
                state.playlistsDrawer.style.left = openToLeft ? 'auto' : (panelWidth + 12) + 'px';
                state.playlistsDrawer.style.right = openToLeft ? (panelWidth + 12) + 'px' : 'auto';
                state.playlistsDrawer.style.transformOrigin = openToLeft ? 'bottom right' : 'bottom left';
                state.playlistsDrawer.style.width = drawerWidth + 'px';
                state.playlistsDrawer.style.maxWidth = drawerWidth + 'px';
              }
            }

            function applyCustomCoverButtonIcon(button) {
              if (!button) {
                return;
              }

              if (button.classList.contains('aplayer-play')) {
                if (button.innerHTML !== COVER_PLAY_ICON) {
                  button.innerHTML = COVER_PLAY_ICON;
                }
                return;
              }

              if (button.classList.contains('aplayer-pause') && button.innerHTML !== COVER_PAUSE_ICON) {
                button.innerHTML = COVER_PAUSE_ICON;
              }
            }

            function syncBubblePlaybackState(player) {
              if (!state.root) {
                return;
              }
              var isPlaying = !!(player && player.audio && !player.audio.paused);
              state.root.classList.toggle('is-playing', isPlaying);
            }

            function applyHoverTitles(aplayer) {
              if (!aplayer) {
                return;
              }

              var title = aplayer.querySelector('.aplayer-title');
              var author = aplayer.querySelector('.aplayer-author');
              var back = aplayer.querySelector('.aplayer-icon-back');
              var play = aplayer.querySelector('.aplayer-icon-play');
              var forward = aplayer.querySelector('.aplayer-icon-forward');
              var volume = aplayer.querySelector('.aplayer-icon-volume-down');
              var order = aplayer.querySelector('.aplayer-icon-order');
              var loop = aplayer.querySelector('.aplayer-icon-loop');
              var menu = aplayer.querySelector('.aplayer-icon-menu');
              var lrc = aplayer.querySelector('.aplayer-icon-lrc');

              if (title) {
                title.title = title.textContent || '';
              }
              if (author) {
                author.title = author.textContent || '';
              }
              if (back) {
                back.title = '上一首';
              }
              if (play) {
                play.title = state.player && state.player.audio && !state.player.audio.paused ? '暂停' : '播放';
              }
              if (forward) {
                forward.title = '下一首';
              }
              if (volume) {
                volume.title = '音量';
              }
              if (order) {
                order.title = '播放顺序';
              }
              if (loop) {
                loop.title = '循环模式';
              }
              if (menu) {
                menu.title = '歌单';
                menu.style.display = Array.isArray(state.playlistsCatalog) && state.playlistsCatalog.length > 0
                  ? 'inline-flex'
                  : 'none';
              }
              if (lrc) {
                lrc.style.display = 'none';
              }

              Array.prototype.slice.call(aplayer.querySelectorAll('.aplayer-list ol li')).forEach(function (item) {
                var itemTitle = item.querySelector('.aplayer-list-title');
                var itemAuthor = item.querySelector('.aplayer-list-author');
                var text = [
                  itemTitle ? itemTitle.textContent.trim() : '',
                  itemAuthor ? itemAuthor.textContent.trim() : ''
                ].filter(Boolean).join(' - ');
                item.title = text;
                if (itemTitle) {
                  itemTitle.title = itemTitle.textContent.trim();
                }
                if (itemAuthor) {
                  itemAuthor.title = itemAuthor.textContent.trim();
                }
              });
            }

            function attachPagerToPanel() {
              if (!state.pager || !state.panel) {
                return;
              }
              if (state.pager.parentNode !== state.panel) {
                state.panel.appendChild(state.pager);
              }
            }

            function applyPlayerLayoutTweaks() {
              if (!state.playerContainer) {
                return;
              }

              var aplayer = state.playerContainer.classList.contains('aplayer')
                ? state.playerContainer
                : state.playerContainer.querySelector('.aplayer');
              if (!aplayer) {
                return;
              }

              var body = aplayer.querySelector('.aplayer-body');
              var pic = aplayer.querySelector('.aplayer-pic');
              var info = aplayer.querySelector('.aplayer-info');
              var music = aplayer.querySelector('.aplayer-music');
              var title = aplayer.querySelector('.aplayer-title');
              var author = aplayer.querySelector('.aplayer-author');
              var controller = aplayer.querySelector('.aplayer-controller');
              var barWrap = aplayer.querySelector('.aplayer-bar-wrap');
              var time = aplayer.querySelector('.aplayer-time');
              var timeInner = aplayer.querySelector('.aplayer-time-inner');
              var button = aplayer.querySelector('.aplayer-button');
              var list = aplayer.querySelector('.aplayer-list');
              var listOl = aplayer.querySelector('.aplayer-list ol');
              var menu = aplayer.querySelector('.aplayer-icon-menu');
              var lrc = aplayer.querySelector('.aplayer-icon-lrc');
              var isMobile = getViewportWidth() <= 640;
              var coverSize = isMobile ? 96 : 112;
              var bodyHeight = isMobile ? 96 : 112;

              if (body) {
                body.style.display = 'flex';
                body.style.alignItems = 'stretch';
                body.style.minHeight = bodyHeight + 'px';
              }
              if (pic) {
                pic.style.float = 'none';
                pic.style.flex = '0 0 ' + coverSize + 'px';
                pic.style.width = coverSize + 'px';
                pic.style.height = coverSize + 'px';
                pic.style.minWidth = coverSize + 'px';
                pic.style.backgroundSize = 'contain';
                pic.style.backgroundRepeat = 'no-repeat';
                pic.style.backgroundPosition = 'center center';
                pic.style.backgroundColor = '#f5f7fb';
              }
              if (info) {
                info.style.display = 'flex';
                info.style.flex = '1 1 auto';
                info.style.flexDirection = 'column';
                info.style.justifyContent = 'space-between';
                info.style.minWidth = '0';
                info.style.marginLeft = '0';
                info.style.height = bodyHeight + 'px';
                info.style.padding = isMobile ? '12px 12px 10px' : '16px 18px 14px';
              }
              if (music) {
                music.style.display = 'flex';
                music.style.flexDirection = 'column';
                music.style.justifyContent = 'flex-start';
                music.style.height = 'auto';
                music.style.minHeight = isMobile ? '34px' : '38px';
                music.style.margin = isMobile ? '0 0 10px' : '0 0 12px';
              }
              if (title) {
                title.style.display = 'block';
                title.style.overflow = 'hidden';
                title.style.textOverflow = 'ellipsis';
                title.style.whiteSpace = 'nowrap';
                title.style.fontSize = isMobile ? '16px' : '18px';
                title.style.lineHeight = '1.35';
              }
              if (author) {
                author.style.display = 'block';
                author.style.marginTop = '4px';
                author.style.overflow = 'hidden';
                author.style.textOverflow = 'ellipsis';
                author.style.whiteSpace = 'nowrap';
                author.style.fontSize = isMobile ? '12px' : '13px';
                author.style.lineHeight = '1.35';
              }
              if (controller) {
                controller.style.display = 'flex';
                controller.style.flexDirection = 'column';
                controller.style.alignItems = 'stretch';
                controller.style.justifyContent = 'flex-end';
                controller.style.gap = isMobile ? '6px' : '8px';
                controller.style.minWidth = '0';
              }
              if (barWrap) {
                barWrap.style.display = 'block';
                barWrap.style.flex = '0 0 auto';
                barWrap.style.width = '100%';
                barWrap.style.marginLeft = '0';
                barWrap.style.marginRight = '0';
                barWrap.style.paddingTop = '0';
                barWrap.style.paddingBottom = '0';
              }
              if (time) {
                time.style.display = 'flex';
                time.style.alignItems = 'center';
                time.style.justifyContent = 'space-between';
                time.style.width = '100%';
                time.style.flex = '1 1 auto';
                time.style.flexWrap = 'nowrap';
                time.style.gap = isMobile ? '6px' : '8px';
                time.style.height = 'auto';
                time.style.paddingTop = '0';
                time.style.paddingLeft = '0';
                time.style.whiteSpace = 'nowrap';
                time.style.fontSize = isMobile ? '12px' : '13px';
                time.style.lineHeight = '1.3';
              }
              if (timeInner) {
                timeInner.style.marginTop = '0';
                timeInner.style.display = 'inline-flex';
                timeInner.style.alignItems = 'center';
                timeInner.style.gap = '4px';
                timeInner.style.whiteSpace = 'nowrap';
                timeInner.style.flex = '0 0 auto';
                timeInner.style.marginRight = 'auto';
              }
              if (button) {
                button.style.display = 'none';
              }
              if (menu) {
                menu.style.display = 'none';
              }
              if (lrc) {
                lrc.style.display = 'none';
              }
              if (list) {
                list.style.maxHeight = 'none';
                list.style.overflowY = 'hidden';
                attachPagerToPanel();
              }
              if (listOl) {
                listOl.style.overflowY = 'hidden';
              }
              applyHoverTitles(aplayer);
            }

            function setOpen(nextOpen) {
              state.open = !!nextOpen;
              if (state.root) {
                state.root.classList.toggle('is-open', state.open);
              }
              if (!state.open) {
                setPlaylistsDrawerOpen(false);
              }
              persistUiState();
            }

            function setPlaylistsDrawerOpen(nextOpen) {
              state.drawerOpen = !!nextOpen && Array.isArray(state.playlistsCatalog)
                && state.playlistsCatalog.length > 0;
              if (state.root) {
                state.root.classList.toggle('is-playlists-open', state.drawerOpen);
              }
              applyPanelLayout();
              persistUiState();
            }

            function togglePlaylistsDrawer() {
              setPlaylistsDrawerOpen(!state.drawerOpen);
            }

            function toggleOpen() {
              var nextOpen = !state.open;
              setOpen(nextOpen);
              if (nextOpen) {
                ensurePlayer();
              }
            }

            function ensureProgressBar() {
              var progress = document.getElementById('halo-navidrome-pjax-progress');
              if (!progress) {
                progress = document.createElement('div');
                progress.id = 'halo-navidrome-pjax-progress';
                document.body.appendChild(progress);
              } else if (progress.parentNode !== document.body) {
                document.body.appendChild(progress);
              }
              state.progress = progress;
            }
        """ + footerHtmlPart3();
    }

    private static String footerHtmlPart3() {
        return """
            function ensureRoot() {
              var root = document.getElementById('halo-navidrome-root');
              if (!root) {
                root = document.createElement('div');
                root.id = 'halo-navidrome-root';
                root.innerHTML = ''
                  + '<button id="halo-navidrome-bubble" type="button" aria-label="Toggle player">♫</button>'
                  + '<div id="halo-navidrome-panel">'
                  + '  <div id="halo-navidrome-status" class="is-hidden">'
                  + '    <div id="halo-navidrome-status-text"></div>'
                  + '    <button id="halo-navidrome-status-action" type="button">重试</button>'
                  + '  </div>'
                  + '  <div id="halo-navidrome-aplayer"></div>'
                  + '  <div id="halo-navidrome-list-pager" class="is-hidden">'
                  + '    <button id="halo-navidrome-page-prev" type="button">上页</button>'
                  + '    <div id="halo-navidrome-list-page-text"></div>'
                  + '    <button id="halo-navidrome-page-next" type="button">下页</button>'
                  + '  </div>'
                  + '  <div id="halo-navidrome-playlists-drawer" class="is-hidden">'
                  + '    <div id="halo-navidrome-playlists-card">'
                  + '      <div id="halo-navidrome-playlists-title">我的歌单</div>'
                  + '      <div id="halo-navidrome-playlists-list"></div>'
                  + '    </div>'
                  + '  </div>'
                  + '</div>';
                document.body.appendChild(root);
              } else if (root.parentNode !== document.body) {
                document.body.appendChild(root);
              }

              state.root = root;
              state.bubble = root.querySelector('#halo-navidrome-bubble');
              state.panel = root.querySelector('#halo-navidrome-panel');
              state.status = root.querySelector('#halo-navidrome-status');
              state.statusText = root.querySelector('#halo-navidrome-status-text');
              state.statusAction = root.querySelector('#halo-navidrome-status-action');
              state.playerContainer = root.querySelector('#halo-navidrome-aplayer');
              state.pager = root.querySelector('#halo-navidrome-list-pager');
              state.pagePrev = root.querySelector('#halo-navidrome-page-prev');
              state.pageNext = root.querySelector('#halo-navidrome-page-next');
              state.pageText = root.querySelector('#halo-navidrome-list-page-text');
              state.playlistsDrawer = root.querySelector('#halo-navidrome-playlists-drawer');
              state.playlistsDrawerTitle = root.querySelector('#halo-navidrome-playlists-title');
              state.playlistsDrawerList = root.querySelector('#halo-navidrome-playlists-list');

              var existingPlayerContainer = state.player && state.player.container;
              if (existingPlayerContainer && state.playerContainer && existingPlayerContainer !== state.playerContainer) {
                existingPlayerContainer.id = 'halo-navidrome-aplayer';
                state.playerContainer.parentNode.replaceChild(existingPlayerContainer, state.playerContainer);
                state.playerContainer = existingPlayerContainer;
              }

              if (state.statusAction && state.statusAction.dataset.bound !== 'true') {
                state.statusAction.dataset.bound = 'true';
                state.statusAction.addEventListener('click', function () {
                  state.playlistsCatalogPromise = null;
                  state.playlistsCatalog = null;
                  state.playlistPromisesById = {};
                  state.assetPromise = null;
                  setOpen(true);
                  ensurePlayer();
                });
              }

              if (state.pagePrev && state.pagePrev.dataset.bound !== 'true') {
                state.pagePrev.dataset.bound = 'true';
                state.pagePrev.addEventListener('click', function (event) {
                  event.preventDefault();
                  event.stopPropagation();
                  setPlaylistPage((state.playlistPage || 1) - 1, { manual: true });
                });
              }

              if (state.pageNext && state.pageNext.dataset.bound !== 'true') {
                state.pageNext.dataset.bound = 'true';
                state.pageNext.addEventListener('click', function (event) {
                  event.preventDefault();
                  event.stopPropagation();
                  setPlaylistPage((state.playlistPage || 1) + 1, { manual: true });
                });
              }

              if (!state.position) {
                var defaults = resolveDefaultPosition();
                state.position = { left: defaults.left, top: defaults.top };
                state.open = defaults.open;
                state.drawerOpen = !!(state.stored || readStoredState()).drawerOpen;
              }

              applyPosition();
              applyPanelLayout();
              setOpen(!!state.open);
              setPlaylistsDrawerOpen(!!state.drawerOpen);
            }

            function renderStatus(kind, message, actionable) {
              if (!state.status || !state.statusText) {
                return;
              }
              state.statusText.textContent = message || '';
              state.status.classList.remove('is-hidden', 'is-loading', 'is-error', 'is-actionable');
              if (kind === 'loading') {
                state.status.classList.add('is-loading');
              }
              if (kind === 'error') {
                state.status.classList.add('is-error');
              }
              if (actionable) {
                state.status.classList.add('is-actionable');
              }
            }

            function clearStatus() {
              if (!state.status) {
                return;
              }
              state.status.classList.add('is-hidden');
              state.status.classList.remove('is-loading', 'is-error', 'is-actionable');
            }

            function getPlaylistItems() {
              if (!state.playerContainer) {
                return [];
              }
              return Array.prototype.slice.call(
                state.playerContainer.querySelectorAll('.aplayer-list ol li')
              );
            }

            function updatePagerVisibility(totalPages) {
              if (!state.pager) {
                return;
              }
              state.pager.classList.toggle('is-hidden', totalPages <= 1);
            }

            function setPlaylistPage(pageNumber, options) {
              options = options || {};
              var items = getPlaylistItems();
              if (!items.length) {
                updatePagerVisibility(0);
                return;
              }

              var totalPages = Math.ceil(items.length / PLAYLIST_PAGE_SIZE);
              var nextPage = Math.min(Math.max(pageNumber || 1, 1), totalPages);
              state.playlistPage = nextPage;
              if (options.manual === true) {
                state.playlistPageManual = true;
              } else if (options.manual === false) {
                state.playlistPageManual = false;
              }
              persistCurrentPlaylistPage();

              items.forEach(function (item, index) {
                var page = Math.floor(index / PLAYLIST_PAGE_SIZE) + 1;
                item.classList.toggle('is-hidden-by-page', page !== nextPage);
                item.style.display = page === nextPage ? '' : 'none';
              });

              updatePagerVisibility(totalPages);
              if (state.pagePrev) {
                state.pagePrev.disabled = nextPage <= 1;
              }
              if (state.pageNext) {
                state.pageNext.disabled = nextPage >= totalPages;
              }
              if (state.pageText) {
                var start = (nextPage - 1) * PLAYLIST_PAGE_SIZE + 1;
                var end = Math.min(nextPage * PLAYLIST_PAGE_SIZE, items.length);
                state.pageText.textContent = nextPage + '/' + totalPages
                  + ' · ' + start + '-' + end + '/' + items.length;
              }
            }

            function syncPlaylistPageToTrack(options) {
              options = options || {};
              if (state.playlistPageManual && options.force !== true) {
                return;
              }
              if (!state.player || !state.player.list) {
                return;
              }
              state.playlistPageManual = false;
              var currentIndex = state.player.list.index || 0;
              var currentPage = Math.floor(currentIndex / PLAYLIST_PAGE_SIZE) + 1;
              setPlaylistPage(currentPage, { manual: false });
            }

            function refreshPlaylistPagination() {
              var items = getPlaylistItems();
              if (!items.length) {
                updatePagerVisibility(0);
                return;
              }

              var totalPages = Math.ceil(items.length / PLAYLIST_PAGE_SIZE);
              var currentPage = state.playlistPage || 1;
              if (!state.playlistPageManual && state.player && state.player.list) {
                currentPage = Math.floor((state.player.list.index || 0) / PLAYLIST_PAGE_SIZE) + 1;
              }
              setPlaylistPage(Math.min(currentPage, totalPages), {
                manual: state.playlistPageManual === true
              });
              applyPlayerLayoutTweaks();
            }

            function bindDrag() {
              if (!state.bubble || state.bubble.dataset.bound === 'true') {
                return;
              }
              state.bubble.dataset.bound = 'true';

              state.bubble.addEventListener('pointerdown', function (event) {
                if (event.pointerType !== 'touch' && event.button !== 0) {
                  return;
                }
                state.drag = {
                  pointerId: event.pointerId,
                  startX: event.clientX,
                  startY: event.clientY,
                  startedAt: Date.now(),
                  originLeft: state.position.left,
                  originTop: state.position.top,
                  moved: false
                };
                state.root.classList.add('is-dragging');
                state.bubble.setPointerCapture(event.pointerId);
              });

              state.bubble.addEventListener('pointermove', function (event) {
                if (!state.drag || state.drag.pointerId !== event.pointerId) {
                  return;
                }
                var nextLeft = state.drag.originLeft + (event.clientX - state.drag.startX);
                var nextTop = state.drag.originTop + (event.clientY - state.drag.startY);
                state.position.left = nextLeft;
                state.position.top = nextTop;
                state.drag.moved = state.drag.moved
                  || Math.abs(event.clientX - state.drag.startX) > 4
                  || Math.abs(event.clientY - state.drag.startY) > 4;
                applyPosition();
              });

              state.bubble.addEventListener('pointerup', function (event) {
                if (!state.drag || state.drag.pointerId !== event.pointerId) {
                  return;
                }
                state.root.classList.remove('is-dragging');
                state.bubble.releasePointerCapture(event.pointerId);
                var moved = state.drag.moved;
                var quickTap = !moved && (Date.now() - state.drag.startedAt) <= QUICK_TAP_THRESHOLD_MS;
                state.drag = null;
                state.ignoreBubbleClickUntil = Date.now() + 350;
                persistUiState();
                if (quickTap) {
                  toggleOpen();
                }
              });

              state.bubble.addEventListener('pointercancel', function () {
                state.root.classList.remove('is-dragging');
                state.drag = null;
                state.ignoreBubbleClickUntil = Date.now() + 350;
                persistUiState();
              });

              state.bubble.addEventListener('click', function () {
                if (state.ignoreBubbleClickUntil && Date.now() < state.ignoreBubbleClickUntil) {
                  return;
                }
                if (state.drag) {
                  return;
                }
                toggleOpen();
              });
            }

            function fetchJson(url, emptyMessagePrefix) {
              return fetch(url).then(function (response) {
                if (!response.ok) {
                  return response.text().then(function (text) {
                    var message = text && text.trim()
                      ? text.trim()
                      : (response.status === 401 || response.status === 403
                        ? '接口未开放匿名访问，请检查角色模板权限。'
                        : emptyMessagePrefix + '失败，状态码：' + response.status);
                    throw new Error(message);
                  });
                }
                return response.json();
              });
            }

            function resolveInitialPlaylistId(playlists) {
              if (!Array.isArray(playlists) || playlists.length === 0) {
                return '';
              }

              var stored = state.stored || readStoredState();
              if (stored && stored.playlistId) {
                var matched = playlists.find(function (playlist) {
                  return playlist && playlist.id === stored.playlistId;
                });
                if (matched) {
                  return matched.id;
                }
              }
              return playlists[0].id || '';
            }

            function renderPlaylistsDrawer() {
              if (!state.playlistsDrawer || !state.playlistsDrawerList) {
                return;
              }

              var playlists = Array.isArray(state.playlistsCatalog) ? state.playlistsCatalog : [];
              if (playlists.length === 0) {
                state.playlistsDrawer.classList.add('is-hidden');
                setPlaylistsDrawerOpen(false);
                applyPanelLayout();
                return;
              }

              state.playlistsDrawer.classList.remove('is-hidden');
              state.playlistsDrawerTitle.textContent = '我的歌单';
              state.playlistsDrawerList.innerHTML = '';

              playlists.forEach(function (playlist, index) {
                var button = document.createElement('button');
                button.type = 'button';
                button.className = 'halo-navidrome-playlist-option';
                if (playlist.id === state.currentPlaylistId) {
                  button.classList.add('is-active');
                }
                button.title = playlist.name || playlist.id || '';

                var indexNode = document.createElement('span');
                indexNode.className = 'halo-navidrome-playlist-option-index';
                indexNode.textContent = String(index + 1);

                var main = document.createElement('span');
                main.className = 'halo-navidrome-playlist-option-main';

                var name = document.createElement('span');
                name.className = 'halo-navidrome-playlist-option-name';
                name.textContent = playlist.name || playlist.id || '未命名歌单';

                var meta = document.createElement('span');
                meta.className = 'halo-navidrome-playlist-option-meta';
                meta.textContent = (playlist.songCount || 0) + ' 首';

                main.appendChild(name);
                main.appendChild(meta);
                button.appendChild(indexNode);
                button.appendChild(main);
                button.addEventListener('click', function () {
                  switchPlaylist(playlist.id);
                });
                state.playlistsDrawerList.appendChild(button);
              });
              applyPanelLayout();
            }

            function loadPlaylistsCatalog() {
              if (Array.isArray(state.playlistsCatalog)) {
                return Promise.resolve(state.playlistsCatalog);
              }

              if (!state.playlistsCatalogPromise) {
                state.playlistsCatalogPromise = fetchJson(
                  '/apis/ext.navidrome/v1/playlists',
                  '歌单目录接口请求'
                ).then(function (data) {
                  state.playlistsCatalog = Array.isArray(data) ? data : [];
                  renderPlaylistsDrawer();
                  return state.playlistsCatalog;
                }).catch(function (error) {
                  state.playlistsCatalogPromise = null;
                  throw error;
                });
              }

              return state.playlistsCatalogPromise;
            }

            function loadPlaylist(playlistId) {
              state.playlistPromisesById = state.playlistPromisesById || {};
              var cacheKey = playlistId || '__default__';
              if (!state.playlistPromisesById[cacheKey]) {
                renderStatus('loading', '正在加载歌单...', false);
                var url = '/apis/ext.navidrome/v1/playlist';
                if (playlistId) {
                  url += '?playlistId=' + encodeURIComponent(playlistId);
                }
                state.playlistPromisesById[cacheKey] = fetchJson(url, '歌单接口请求')
                  .then(function (data) {
                    return Array.isArray(data) ? data : [];
                  })
                  .catch(function (error) {
                    delete state.playlistPromisesById[cacheKey];
                    throw error;
                  });
              }
              return state.playlistPromisesById[cacheKey];
            }

            function loadStylesheet(urls, index) {
              if (document.querySelector('link[data-halo-navidrome-aplayer-css="true"]')) {
                return Promise.resolve();
              }
              if (index >= urls.length) {
                return Promise.reject(new Error('APlayer 样式资源加载失败。'));
              }

              return new Promise(function (resolve, reject) {
                var link = document.createElement('link');
                link.rel = 'stylesheet';
                link.href = urls[index];
                link.dataset.haloNavidromeAplayerCss = 'true';
                link.onload = resolve;
                link.onerror = function () {
                  if (link.parentNode) {
                    link.parentNode.removeChild(link);
                  }
                  loadStylesheet(urls, index + 1).then(resolve).catch(reject);
                };
                document.head.appendChild(link);
              });
            }

            function loadScript(urls, index) {
              if (window.APlayer) {
                return Promise.resolve();
              }
              if (index >= urls.length) {
                return Promise.reject(new Error('APlayer 脚本资源加载失败。'));
              }

              return new Promise(function (resolve, reject) {
                var script = document.createElement('script');
                script.src = urls[index];
                script.async = true;
                script.dataset.haloNavidromeAplayerJs = 'true';
                script.onload = function () {
                  if (window.APlayer) {
                    resolve();
                    return;
                  }
                  reject(new Error('APlayer 脚本已加载，但全局对象不存在。'));
                };
                script.onerror = function () {
                  if (script.parentNode) {
                    script.parentNode.removeChild(script);
                  }
                  loadScript(urls, index + 1).then(resolve).catch(reject);
                };
                document.body.appendChild(script);
              });
            }

            function withTimeout(promise, timeoutMs, message) {
              return new Promise(function (resolve, reject) {
                var settled = false;
                var timer = window.setTimeout(function () {
                  if (settled) {
                    return;
                  }
                  settled = true;
                  reject(new Error(message));
                }, timeoutMs);

                promise.then(function (result) {
                  if (settled) {
                    return;
                  }
                  settled = true;
                  window.clearTimeout(timer);
                  resolve(result);
                }).catch(function (error) {
                  if (settled) {
                    return;
                  }
                  settled = true;
                  window.clearTimeout(timer);
                  reject(error);
                });
              });
            }

            function ensureAPlayerAssets() {
              if (!state.assetPromise) {
                renderStatus('loading', '正在加载播放器资源...', false);
                state.assetPromise = withTimeout(
                  loadStylesheet(APLAYER_CSS_URLS, 0).then(function () {
                    return window.APlayer ? Promise.resolve() : loadScript(APLAYER_SCRIPT_URLS, 0);
                  }),
                  ASSET_LOAD_TIMEOUT,
                  '播放器资源加载超时，请检查 CDN 连通性。'
                ).catch(function (error) {
                  state.assetPromise = null;
                  throw error;
                });
              }
              return state.assetPromise;
            }

            function bindCustomMenuButton(player) {
              if (!player || !player.template || !player.template.menu) {
                return;
              }

              var menu = player.template.menu;
              if (menu.dataset.haloNavidromeBound === 'true') {
                menu.style.display = Array.isArray(state.playlistsCatalog) && state.playlistsCatalog.length > 0
                  ? 'inline-flex'
                  : 'none';
                return;
              }

              var replacement = menu.cloneNode(true);
              replacement.dataset.haloNavidromeBound = 'true';
              replacement.title = '歌单';
              replacement.style.display = Array.isArray(state.playlistsCatalog) && state.playlistsCatalog.length > 0
                ? 'inline-flex'
                : 'none';
              replacement.addEventListener('click', function (event) {
                event.preventDefault();
                event.stopPropagation();
                togglePlaylistsDrawer();
              });
              menu.parentNode.replaceChild(replacement, menu);
              player.template.menu = replacement;
            }

            function resetPlayerContainer() {
              if (!state.playerContainer || !state.playerContainer.parentNode) {
                return;
              }
              var nextContainer = document.createElement('div');
              nextContainer.id = 'halo-navidrome-aplayer';
              state.playerContainer.parentNode.replaceChild(nextContainer, state.playerContainer);
              state.playerContainer = nextContainer;
            }

            function mountPlayer(playlistId, playlist, restoreStoredState, shouldAutoplay) {
              if (!playlist.length) {
                renderStatus('error', '当前歌单为空，请检查 playlistId 或 Navidrome 返回结果。', true);
                return null;
              }
              if (!state.playerContainer) {
                throw new Error('播放器挂载点初始化失败。');
              }

              state.currentPlaylistId = playlistId || '';
              var rememberedPage = getRememberedPlaylistPage(state.currentPlaylistId);
              state.playlistPage = rememberedPage;
              state.playlistPageManual = rememberedPage > 1;
              state.pendingSeek = null;
              state.player = new APlayer({
                container: state.playerContainer,
                fixed: false,
                autoplay: false,
                preload: 'none',
                storageName: APLAYER_STORAGE_KEY,
                audio: playlist
              });
              syncBubblePlaybackState(state.player);
              applyPlayerLayoutTweaks();
              syncDisplayedTrackDuration(state.player);
              bindCustomProgressBar(state.player);
              bindCustomMenuButton(state.player);
              bindPlayerPersistence(state.player);
              if (restoreStoredState) {
                restorePlayerState(state.player, playlist);
              } else if (shouldAutoplay) {
                window.setTimeout(function () {
                  if (state.player) {
                    state.player.play();
                  }
                }, 0);
              }
              window.setTimeout(function () {
                refreshPlaylistPagination();
                renderPlaylistsDrawer();
                applyPlayerLayoutTweaks();
              }, 60);
              clearStatus();
              persistUiState();
              return state.player;
            }

            function switchPlaylist(playlistId) {
              if (!playlistId) {
                return Promise.resolve(null);
              }
              if (state.currentPlaylistId === playlistId && state.player) {
                setPlaylistsDrawerOpen(false);
                return Promise.resolve(state.player);
              }

              var shouldAutoplay = !!(state.player && state.player.audio && !state.player.audio.paused);
              patchStoredPlaybackState(playlistId, {
                trackIndex: 0,
                currentTime: 0,
                paused: !shouldAutoplay
              });
              renderStatus('loading', '正在切换歌单...', false);
              syncBubblePlaybackState(null);

              return loadPlaylist(playlistId).then(function (playlist) {
                if (state.player) {
                  try {
                    state.player.destroy();
                  } catch (error) {
                    console.warn('[navidrome-player] Failed to destroy previous player.', error);
                  }
                }
                state.player = null;
                state.playerEventsBound = false;
                state.pendingRestore = null;
                resetPlayerContainer();
                var nextPlayer = mountPlayer(playlistId, playlist, false, shouldAutoplay);
                setPlaylistsDrawerOpen(false);
                return nextPlayer;
              }).catch(function (error) {
                renderStatus('error', error && error.message ? error.message : '切换歌单失败。', true);
                console.error('[navidrome-player]', error);
                return null;
              });
            }
        """ + footerHtmlPart4();
    }

    private static String footerHtmlPart4() {
        return """
            function ensurePlayer() {
              if (state.player) {
                clearStatus();
                return Promise.resolve(state.player);
              }
              if (state.playerPromise) {
                return state.playerPromise;
              }

              state.playerPromise = Promise.all([
                ensureAPlayerAssets(),
                loadPlaylistsCatalog()
              ]).then(function (results) {
                var playlists = results[1];
                var playlistId = resolveInitialPlaylistId(playlists);
                state.currentPlaylistId = playlistId;
                return loadPlaylist(playlistId).then(function (playlist) {
                  return mountPlayer(playlistId, playlist, true, false);
                });
              }).catch(function (error) {
                state.player = null;
                renderStatus('error', error && error.message ? error.message : '播放器初始化失败。', true);
                console.error('[navidrome-player]', error);
                return null;
              }).finally(function () {
                state.playerPromise = null;
              });

              return state.playerPromise;
            }

            function normalizeTrackIndex(trackIndex, total) {
              if (!Number.isInteger(trackIndex) || total <= 0) {
                return 0;
              }
              if (trackIndex < 0) {
                return 0;
              }
              if (trackIndex >= total) {
                return total - 1;
              }
              return trackIndex;
            }

            function restorePlayerState(player, playlist) {
              var stored = state.stored || readStoredState();
              if (!stored || !playlist.length) {
                return;
              }
              if (stored.playlistId && state.currentPlaylistId && stored.playlistId !== state.currentPlaylistId) {
                return;
              }
              var playbackState = getStoredPlaybackState(state.currentPlaylistId);
              if (!playbackState && stored.playlistId && stored.playlistId === state.currentPlaylistId) {
                playbackState = stored;
              }
              if (!playbackState) {
                return;
              }

              if (typeof stored.volume === 'number' && Number.isFinite(stored.volume)) {
                player.volume(stored.volume, true);
              }

              state.pendingRestore = {
                index: normalizeTrackIndex(playbackState.trackIndex, playlist.length),
                currentTime: typeof playbackState.currentTime === 'number'
                  && playbackState.currentTime > 0
                  ? playbackState.currentTime
                  : 0,
                shouldPlay: playbackState.paused === false,
                applied: false
              };

              if (player.list.index !== state.pendingRestore.index) {
                player.list.switch(state.pendingRestore.index);
              }

              if (state.pendingRestore.currentTime > 0) {
                previewSeekPosition(
                  player,
                  state.pendingRestore.currentTime,
                  getCurrentTrackDuration(player)
                );
              }

              scheduleStoredPlaybackPreview(player);
              attemptApplyRestore(player);
            }

            function attemptApplyRestore(player) {
              if (state.player !== player) {
                return;
              }
              var pending = state.pendingRestore;
              if (!pending || pending.applied || !player || !player.audio) {
                return;
              }
              if (player.list.index !== pending.index) {
                return;
              }
              if (player.audio.readyState < 1) {
                return;
              }

              pending.applied = true;

              if (pending.currentTime > 0) {
                player.seek(pending.currentTime);
              }

              if (pending.shouldPlay) {
                player.play();
              } else {
                player.pause();
              }

              state.pendingRestore = null;
              window.setTimeout(function () {
                persistPlaybackState();
              }, 0);
            }

            function bindPlayerPersistence(player) {
              if (state.playerEventsBound) {
                return;
              }
              state.playerEventsBound = true;

              player.on('listswitch', function () {
                if (state.player !== player) {
                  return;
                }
                window.setTimeout(function () {
                  if (state.player !== player) {
                    return;
                  }
                  syncPlaylistPageToTrack();
                  state.pendingSeek = null;
                  syncDisplayedTrackDuration(player);
                  applyStoredPlaybackPreview(player);
                  syncBubblePlaybackState(player);
                  applyPlayerLayoutTweaks();
                  schedulePersistPlayback();
                }, 0);
              });
              player.on('play', function () {
                if (state.player !== player) {
                  return;
                }
                if (state.pendingRestore) {
                  state.pendingRestore.shouldPlay = true;
                  if (state.pendingRestore.currentTime > 0) {
                    previewSeekPosition(
                      player,
                      state.pendingRestore.currentTime,
                      getCurrentTrackDuration(player)
                    );
                  }
                }
                applyPendingSeek(player);
                syncBubblePlaybackState(player);
                applyPlayerLayoutTweaks();
                schedulePersistPlayback();
              });
              player.on('pause', function () {
                if (state.player !== player) {
                  return;
                }
                if (state.pendingRestore) {
                  state.pendingRestore.shouldPlay = false;
                }
                syncBubblePlaybackState(player);
                applyPlayerLayoutTweaks();
                persistPlaybackState();
              });
              player.on('timeupdate', function () {
                if (state.player !== player) {
                  return;
                }
                if (!state.pendingRestore) {
                  schedulePersistPlayback();
                }
              });
              player.on('volumechange', function () {
                if (state.player !== player) {
                  return;
                }
                persistPlaybackState();
              });
              player.on('loadedmetadata', function () {
                if (state.player !== player) {
                  return;
                }
                syncDisplayedTrackDuration(player);
                applyPendingSeek(player);
                attemptApplyRestore(player);
              });
              player.on('canplay', function () {
                if (state.player !== player) {
                  return;
                }
                syncDisplayedTrackDuration(player);
                applyPendingSeek(player);
                attemptApplyRestore(player);
              });
              player.on('durationchange', function () {
                if (state.player !== player) {
                  return;
                }
                syncDisplayedTrackDuration(player);
                applyPendingSeek(player);
              });
              player.on('ended', function () {
                if (state.player !== player) {
                  return;
                }
                syncBubblePlaybackState(player);
                schedulePersistPlayback();
              });

              window.addEventListener('pagehide', persistAllState);
              document.addEventListener('visibilitychange', function () {
                if (document.hidden) {
                  persistAllState();
                }
              });
            }

            function copyAttributes(source, target) {
              Array.prototype.slice.call(target.attributes).forEach(function (attribute) {
                if (!source.hasAttribute(attribute.name)) {
                  target.removeAttribute(attribute.name);
                }
              });

              Array.prototype.slice.call(source.attributes).forEach(function (attribute) {
                target.setAttribute(attribute.name, attribute.value);
              });
            }

            function collectHeadAssets(documentFragment, selector, keyResolver) {
              return Array.prototype.slice.call(documentFragment.head.querySelectorAll(selector))
                .map(function (node) {
                  return {
                    key: keyResolver(node),
                    node: node
                  };
                })
                .filter(function (entry) {
                  return !!entry.key;
                });
            }

            function ensureHeadStyles(documentFragment) {
              var stylesheetAssets = collectHeadAssets(
                documentFragment,
                'link[rel="stylesheet"]',
                function (node) { return node.getAttribute('href'); }
              );
              var inlineStyles = collectHeadAssets(
                documentFragment,
                'style',
                function (node) { return 'inline:' + node.textContent; }
              );
              var assetEntries = stylesheetAssets.concat(inlineStyles);

              return assetEntries.reduce(function (promise, entry) {
                return promise.then(function () {
                  var currentSelector = entry.node.tagName.toLowerCase() === 'link'
                    ? 'link[rel="stylesheet"][href="' + entry.key.replace(/"/g, '\\"') + '"]'
                    : 'style[data-halo-navidrome-style-key="' + btoa(unescape(encodeURIComponent(entry.key))).replace(/=/g, '') + '"]';

                  if (document.head.querySelector(currentSelector)) {
                    return Promise.resolve();
                  }

                  return new Promise(function (resolve, reject) {
                    var clone = entry.node.cloneNode(true);
                    if (clone.tagName && clone.tagName.toLowerCase() === 'style') {
                      clone.setAttribute(
                        'data-halo-navidrome-style-key',
                        btoa(unescape(encodeURIComponent(entry.key))).replace(/=/g, '')
                      );
                      document.head.appendChild(clone);
                      resolve();
                      return;
                    }

                    clone.onload = function () { resolve(); };
                    clone.onerror = function () { reject(new Error('Failed to load page stylesheet: ' + entry.key)); };
                    document.head.appendChild(clone);
                  });
                });
              }, Promise.resolve());
            }

            function ensureHeadScripts(documentFragment) {
              var scriptEntries = collectHeadAssets(
                documentFragment,
                'script[src]',
                function (node) { return node.getAttribute('src'); }
              );

              return scriptEntries.reduce(function (promise, entry) {
                return promise.then(function () {
                  var selector = 'script[src="' + entry.key.replace(/"/g, '\\"') + '"]';
                  if (document.head.querySelector(selector) || document.body.querySelector(selector)) {
                    return Promise.resolve();
                  }

                  return new Promise(function (resolve, reject) {
                    var clone = entry.node.cloneNode(true);
                    clone.async = false;
                    clone.onload = function () { resolve(); };
                    clone.onerror = function () { reject(new Error('Failed to load page script: ' + entry.key)); };
                    document.head.appendChild(clone);
                  });
                });
              }, Promise.resolve());
            }

            function syncHead(documentFragment) {
              document.title = documentFragment.title || document.title;

              [
                'meta[name="description"]',
                'link[rel="canonical"]',
                'meta[property="og:title"]',
                'meta[property="og:description"]',
                'meta[property="og:url"]'
              ].forEach(function (selector) {
                var currentNode = document.head.querySelector(selector);
                var incomingNode = documentFragment.head.querySelector(selector);

                if (!currentNode && incomingNode) {
                  document.head.appendChild(incomingNode.cloneNode(true));
                  return;
                }

                if (currentNode && !incomingNode) {
                  currentNode.remove();
                  return;
                }

                if (currentNode && incomingNode) {
                  currentNode.replaceWith(incomingNode.cloneNode(true));
                }
              });
            }

            function normalizeNavPath(urlLike) {
              var url;
              try {
                url = urlLike instanceof URL ? urlLike : new URL(urlLike, window.location.origin);
              } catch (error) {
                return '/';
              }

              var path = (url.pathname || '/').replace(/\\/{2,}/g, '/');
              if (!path) {
                return '/';
              }
              if (path !== '/') {
                path = path.replace(/\\/+$/, '');
              }
              return path || '/';
            }

            function clearSidebarActiveState(navRoot) {
              Array.prototype.slice.call(navRoot.querySelectorAll(
                'a.sidebar-nav-item, a.dropdown-item, .nav-item-wrapper, .has-submenu, .dropdown-menu, li'
              )).forEach(function (node) {
                node.classList.remove('active', 'current', 'is-active', 'is-open', 'open', 'expanded');
                if (node.tagName && node.tagName.toLowerCase() === 'a') {
                  node.removeAttribute('aria-current');
                }
              });
            }

            function scoreSidebarLink(link, currentUrl) {
              var href = link.getAttribute('href');
              if (!href || href.charAt(0) === '#') {
                return -1;
              }

              var linkUrl;
              try {
                linkUrl = new URL(link.href, currentUrl.href);
              } catch (error) {
                return -1;
              }

              if (linkUrl.origin !== currentUrl.origin) {
                return -1;
              }

              var currentPath = normalizeNavPath(currentUrl);
              var linkPath = normalizeNavPath(linkUrl);
              if (linkPath === '/' && currentPath !== '/') {
                return currentPath === '/' ? 5000 : -1;
              }

              var currentSearch = currentUrl.search || '';
              var linkSearch = linkUrl.search || '';
              if (linkPath === currentPath) {
                return (currentSearch === linkSearch ? 5000 : 4200) + linkPath.length;
              }

              if (linkPath !== '/' && currentPath.indexOf(linkPath + '/') === 0) {
                return 2000 + linkPath.length;
              }

              return -1;
            }

            function activateSidebarLink(link) {
              if (!link) {
                return;
              }

              link.classList.add('active', 'current', 'is-active');
              link.setAttribute('aria-current', 'page');

              var listItem = link.closest('li');
              if (listItem) {
                listItem.classList.add('active', 'current', 'is-active');
              }

              var wrapper = link.closest('.nav-item-wrapper');
              if (wrapper) {
                wrapper.classList.add('active', 'current', 'is-active');
              }

              var submenu = link.closest('.has-submenu');
              if (submenu) {
                submenu.classList.add('active', 'current', 'is-active', 'is-open', 'open', 'expanded');
                var parentLink = submenu.querySelector('.sidebar-nav-item.has-dropdown');
                if (parentLink) {
                  parentLink.classList.add('active', 'current', 'is-active');
                }
                var dropdown = submenu.querySelector('.dropdown-menu');
                if (dropdown) {
                  dropdown.classList.add('active', 'is-open', 'open', 'expanded');
                }
              }
            }

            function syncThemeNavActiveState() {
              var currentUrl;
              try {
                currentUrl = new URL(window.location.href);
              } catch (error) {
                return;
              }

              Array.prototype.slice.call(document.querySelectorAll('.sidebar-nav')).forEach(function (navRoot) {
                var links = Array.prototype.slice.call(
                  navRoot.querySelectorAll('a.sidebar-nav-item[href], a.dropdown-item[href]')
                );

                if (!links.length) {
                  return;
                }

                clearSidebarActiveState(navRoot);

                var bestLink = null;
                var bestScore = -1;
                links.forEach(function (link) {
                  var score = scoreSidebarLink(link, currentUrl);
                  if (score > bestScore) {
                    bestScore = score;
                    bestLink = link;
                  }
                });

                if (bestLink) {
                  activateSidebarLink(bestLink);
                }
              });
            }

            function scheduleThemeNavSync() {
              if (state.navSyncTimers && state.navSyncTimers.length) {
                state.navSyncTimers.forEach(function (timerId) {
                  window.clearTimeout(timerId);
                });
              }
              state.navSyncTimers = [];

              syncThemeNavActiveState();
              [80, 220, 480].forEach(function (delay) {
                var timerId = window.setTimeout(syncThemeNavActiveState, delay);
                state.navSyncTimers.push(timerId);
              });
            }

            function executeScripts(container) {
              Array.prototype.slice.call(container.querySelectorAll('script')).forEach(function (script) {
                var replacement = document.createElement('script');
                Array.prototype.slice.call(script.attributes).forEach(function (attribute) {
                  replacement.setAttribute(attribute.name, attribute.value);
                });
                replacement.textContent = script.textContent;
                script.parentNode.replaceChild(replacement, script);
              });
            }

            function normalizedClassName(node) {
              if (!node || !node.classList) {
                return '';
              }
              return Array.prototype.slice.call(node.classList)
                .filter(function (className) {
                  return className
                    && !/^(is-|has-|active|current|router|loading|loaded|enter|leave)/.test(className);
                })
                .sort()
                .join('.');
            }

            function nodeFingerprint(node) {
              if (!node || !node.tagName) {
                return '';
              }
              return node.tagName.toLowerCase()
                + '#' + (node.id || '')
                + '.' + normalizedClassName(node);
            }

            function childFingerprintSignature(node) {
              if (!node) {
                return '';
              }
              return Array.prototype.slice.call(node.children)
                .filter(function (child) {
                  return child.id !== 'halo-navidrome-root';
                })
                .map(function (child) {
                  return nodeFingerprint(child);
                })
                .join('|');
            }

            function isPjaxLayoutCompatible(currentContainer, nextContainer) {
              if (!currentContainer || !nextContainer) {
                return false;
              }

              var currentParent = currentContainer.parentElement;
              var nextParent = nextContainer.parentElement;
              for (var depth = 0; depth < 2; depth++) {
                if (!currentParent || !nextParent) {
                  break;
                }
                if (currentParent.tagName.toLowerCase() === 'body'
                  || nextParent.tagName.toLowerCase() === 'body') {
                  break;
                }
                if (nodeFingerprint(currentParent) !== nodeFingerprint(nextParent)) {
                  return false;
                }
                if (Math.abs(currentParent.childElementCount - nextParent.childElementCount) > 1) {
                  return false;
                }
                currentParent = currentParent.parentElement;
                nextParent = nextParent.parentElement;
              }
              return true;
            }

            function promoteSwapTarget(target) {
              if (!target || !target.current || !target.next) {
                return target;
              }

              var promotedCurrent = target.current;
              var promotedNext = target.next;
              while (
                promotedCurrent.parentElement
                && promotedNext.parentElement
                && promotedCurrent.parentElement.tagName
                && promotedNext.parentElement.tagName
              ) {
                var currentParent = promotedCurrent.parentElement;
                var nextParent = promotedNext.parentElement;
                var currentParentTag = currentParent.tagName.toLowerCase();
                var nextParentTag = nextParent.tagName.toLowerCase();
                if (currentParentTag === 'body' || currentParentTag === 'html'
                  || nextParentTag === 'body' || nextParentTag === 'html') {
                  break;
                }
                if (nodeFingerprint(currentParent) !== nodeFingerprint(nextParent)) {
                  break;
                }
                if (childFingerprintSignature(currentParent) !== childFingerprintSignature(nextParent)) {
                  break;
                }
                promotedCurrent = currentParent;
                promotedNext = nextParent;
              }

              return {
                selector: target.selector,
                current: promotedCurrent,
                next: promotedNext
              };
            }

            function resolveSwapTarget(newDocument) {
              var preferred = state.swapSelector ? [state.swapSelector] : [];
              var selectors = preferred.concat(CONTAINER_SELECTORS.filter(function (selector) {
                return preferred.indexOf(selector) === -1;
              }));

              for (var i = 0; i < selectors.length; i++) {
                var selector = selectors[i];
                var currentContainer = document.querySelector(selector);
                var nextContainer = newDocument.querySelector(selector);
                if (currentContainer && nextContainer && currentContainer.id !== 'halo-navidrome-root') {
                  if (!isPjaxLayoutCompatible(currentContainer, nextContainer)) {
                    continue;
                  }
                  return promoteSwapTarget({
                    selector: selector,
                    current: currentContainer,
                    next: nextContainer
                  });
                }
              }

              return null;
            }

            function finishProgress() {
              if (!state.progress) {
                return;
              }
              state.progress.classList.remove('is-active');
              state.progress.classList.add('is-done');
              window.setTimeout(function () {
                if (state.progress) {
                  state.progress.classList.remove('is-done');
                }
              }, 180);
            }

            function beginProgress() {
              ensureProgressBar();
              state.progress.classList.remove('is-done');
              state.progress.classList.add('is-active');
            }

            function canHandleLink(anchor, event) {
              if (!anchor || event.defaultPrevented) {
                return false;
              }
              if (event.button !== 0 || event.metaKey || event.ctrlKey || event.shiftKey || event.altKey) {
                return false;
              }
              if (anchor.target && anchor.target !== '_self') {
                return false;
              }
              if (anchor.hasAttribute('download') || anchor.closest('[data-no-pjax]')) {
                return false;
              }

              var href = anchor.getAttribute('href');
              if (!href || href.charAt(0) === '#') {
                return false;
              }
              if (/^(mailto:|tel:|javascript:)/i.test(href)) {
                return false;
              }

              var url = new URL(anchor.href, window.location.href);
              if (url.origin !== window.location.origin) {
                return false;
              }
              if (url.pathname.startsWith('/console') || url.pathname.startsWith('/uc')) {
                return false;
              }
              if (url.pathname === window.location.pathname && url.search === window.location.search) {
                return false;
              }

              return true;
            }

            function fallbackNavigation(url) {
              window.location.href = url;
            }

            function navigate(url, options) {
              options = options || {};
              if (state.abortController) {
                state.abortController.abort();
              }

              persistPlaybackState();

              beginProgress();
              document.body.classList.add('halo-navidrome-pjax-loading');
              document.dispatchEvent(new CustomEvent(PJAX_EVENT_PREFIX + ':start', {
                detail: { url: url }
              }));

              state.abortController = new AbortController();

              return fetch(url, {
                method: 'GET',
                credentials: 'same-origin',
                headers: {
                  'X-Requested-With': 'halo-navidrome-pjax'
                },
                signal: state.abortController.signal
              })
                .then(function (response) {
                  if (!response.ok) {
                    throw new Error('Unexpected response status: ' + response.status);
                  }
                  return response.text();
                })
                .then(function (html) {
                  var parser = new DOMParser();
                  var nextDocument = parser.parseFromString(html, 'text/html');
                  var swapTarget = resolveSwapTarget(nextDocument);
                  if (!swapTarget) {
                    throw new Error('Unable to find a common PJAX container.');
                  }

                  state.swapSelector = swapTarget.selector;
                  return ensureHeadStyles(nextDocument)
                    .then(function () {
                      copyAttributes(nextDocument.documentElement, document.documentElement);
                      copyAttributes(nextDocument.body, document.body);
                      copyAttributes(swapTarget.next, swapTarget.current);
                      swapTarget.current.innerHTML = swapTarget.next.innerHTML;

                      syncHead(nextDocument);
                      executeScripts(swapTarget.current);
                      ensureMounted();
                      scheduleThemeNavSync();
                      ensureHeadScripts(nextDocument)
                        .then(function () {
                          scheduleThemeNavSync();
                        })
                        .catch(function (error) {
                          console.warn('[navidrome-player] Deferred PJAX head script load failed.', error);
                          scheduleThemeNavSync();
                        });

                      if (!options.replace) {
                        window.history.pushState({ url: url }, '', url);
                      }

                      if (window.location.hash) {
                        var target = document.getElementById(window.location.hash.slice(1));
                        if (target) {
                          target.scrollIntoView();
                        }
                      } else if (!options.preserveScroll) {
                        window.scrollTo(0, 0);
                      }

                      document.dispatchEvent(new CustomEvent(PJAX_EVENT_PREFIX + ':complete', {
                        detail: { url: url, container: state.swapSelector }
                      }));
                    });
                })
                .catch(function (error) {
                  if (error && error.name === 'AbortError') {
                    return;
                  }
                  console.error('[navidrome-player] PJAX navigation failed.', error);
                  fallbackNavigation(url);
                })
                .finally(function () {
                  document.body.classList.remove('halo-navidrome-pjax-loading');
                  finishProgress();
                });
            }

            function bindPjax() {
              if (state.pjaxBound) {
                return;
              }
              state.pjaxBound = true;

              document.addEventListener('click', function (event) {
                var anchor = event.target.closest('a');
                if (!canHandleLink(anchor, event)) {
                  return;
                }
                event.preventDefault();
                navigate(anchor.href);
              }, true);

              window.addEventListener('popstate', function () {
                navigate(window.location.href, {
                  replace: true,
                  preserveScroll: false
                });
              });

              window.addEventListener('resize', function () {
                applyPosition();
                applyPanelLayout();
                applyPlayerLayoutTweaks();
                persistUiState();
              });
            }

            function scheduleThemeCompatibleRefresh() {
              if (state.themeCompatibleRefreshTimer) {
                window.clearTimeout(state.themeCompatibleRefreshTimer);
              }

              state.themeCompatibleRefreshTimer = window.setTimeout(function () {
                ensureMounted();
                scheduleThemeNavSync();
                applyPosition();
                applyPanelLayout();
                applyPlayerLayoutTweaks();
                if (state.player) {
                  bindCustomProgressBar(state.player);
                  bindCustomMenuButton(state.player);
                  refreshPlaylistPagination();
                  syncDisplayedTrackDuration(state.player);
                  syncBubblePlaybackState(state.player);
                }
              }, 36);
            }

            function bindThemeCompatiblePjax() {
              if (state.themeCompatibleBound) {
                return;
              }
              state.themeCompatibleBound = true;

              [
                'pjax:success',
                'pjax:complete',
                'turbo:load',
                'turbo:render',
                'swup:contentReplaced',
                'astro:after-swap',
                'astro:page-load',
                'livewire:navigated',
                'inertia:finish'
              ].forEach(function (eventName) {
                document.addEventListener(eventName, scheduleThemeCompatibleRefresh);
                window.addEventListener(eventName, scheduleThemeCompatibleRefresh);
              });

              window.addEventListener('pageshow', scheduleThemeCompatibleRefresh);
              window.addEventListener('resize', function () {
                applyPosition();
                applyPanelLayout();
                applyPlayerLayoutTweaks();
                persistUiState();
              });

              if (!state.themeCompatibleObserver && typeof MutationObserver !== 'undefined' && document.body) {
                state.themeCompatibleObserver = new MutationObserver(function () {
                  var rootMissing = !document.getElementById('halo-navidrome-root');
                  var detachedPlayer = !!(state.player && state.player.container
                    && !document.body.contains(state.player.container));
                  if (rootMissing || detachedPlayer) {
                    scheduleThemeCompatibleRefresh();
                  }
                });
                state.themeCompatibleObserver.observe(document.body, {
                  childList: true
                });
              }
            }

            function activatePjaxMode(mode) {
              state.pjaxMode = resolvePjaxMode(mode);
              if (state.pjaxMode === 'plugin') {
                bindPjax();
                return;
              }
              if (state.pjaxMode === 'theme-compatible') {
                bindThemeCompatiblePjax();
              }
            }

            function ensureMounted() {
              ensureProgressBar();
              ensureRoot();
              bindDrag();
              if (state.open || (state.stored && state.stored.paused === false)) {
                ensurePlayer();
              }
            }

            state.activatePjaxMode = activatePjaxMode;
            state.ensureMounted = ensureMounted;
            state.stored = readStoredState();
            state.position = null;
            state.open = false;

            ensureMounted();
            activatePjaxMode(state.pjaxMode);
            scheduleThemeNavSync();
          })();
        </script>
        <!-- navidrome-player end -->
        """;
    }

    private final ReactiveSettingFetcher settingFetcher;

    public NavidromeFooterProcessor(ReactiveSettingFetcher settingFetcher) {
        this.settingFetcher = settingFetcher;
    }

    private static String renderFooterHtml(NavidromeSetting setting) {
        return FOOTER_HTML.replace(PJAX_MODE_PLACEHOLDER, escapeJsString(setting.pjaxMode()));
    }

    private static String escapeJsString(String value) {
        if (value == null) {
            return "";
        }
        return value
            .replace("\\", "\\\\")
            .replace("'", "\\'")
            .replace("\r", "")
            .replace("\n", "\\n");
    }

    @Override
    public Mono<Void> process(
        ITemplateContext context,
        IProcessableElementTag tag,
        IElementTagStructureHandler structureHandler,
        IModel model
    ) {
        return settingFetcher.fetch(NavidromeSetting.GROUP, NavidromeSetting.class)
            .switchIfEmpty(Mono.just(NavidromeSetting.EMPTY))
            .filter(setting -> setting.enablePlayer() && setting.isConfigured())
            .doOnNext(setting -> model.add(context.getModelFactory().createText(renderFooterHtml(setting))))
            .then();
    }
}
