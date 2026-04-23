# Publishing Checklist

这个文件用于整理“上传到 GitHub 并加入 Halo 应用市场”前需要完成的事项。

## 1. 仓库基础信息

发布前请先确认以下内容已经更新为你自己的信息：

- `src/main/resources/plugin.yaml`
  - `author.name`
  - `author.website`
  - `homepage`
  - `repo`
  - `issues`
  - `logo`

## 2. GitHub 仓库准备

- 创建公开仓库
- 推送源码
- 补齐：
  - `README.md`
  - `LICENSE`
  - 截图或演示 GIF
- 给仓库加 topic：
  - `halo-plugin`

## 3. GitHub Release

推荐按语义化版本发布：

- `4.7.0`
- `4.7.1`
- `4.8.0`
- `5.0.0`

发布 Release 后，`release.yml` 会自动：

- 构建插件
- 上传 `build/libs/*.jar` 到 Release Assets

## 4. Halo 应用市场

根据 Halo 官方文档，当前上架流程是：

1. 向 `halo-sigs/awesome-halo` 发起 PR
2. 在 PR 中提交应用信息
3. 勾选“上架到 Halo 应用市场”
4. 等待官方审核

如果你希望后续自己管理应用和版本，请在 PR 描述里提供你的 Halo 官网用户名。

## 5. 后续自动发布到应用市场

在拿到应用市场管理权限后，再考虑自动同步。

官方文档要求：

1. GitHub 仓库里准备好 CI/CD
2. 获取应用市场 `app-id`
3. 在 Halo 官网创建个人令牌
4. GitHub Secrets 中添加：
   - `HALO_PAT`

然后再把当前仓库的发布流程升级到 Halo 官方推荐的应用市场自动发布模式。

## 官方文档

- Halo 发布应用文档：
  - https://docs.halo.run/developer-guide/appendix/publish-app/
- Halo 应用列表仓库：
  - https://github.com/halo-sigs/awesome-halo

