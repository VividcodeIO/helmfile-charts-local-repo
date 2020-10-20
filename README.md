# Helmfile charts local repo

[![Docker Repository on Quay](https://quay.io/repository/alexcheng1982/helmfile-charts-local-repo/status "Docker Repository on Quay")](https://quay.io/repository/alexcheng1982/helmfile-charts-local-repo)

Generate [ChartMuseum](https://chartmuseum.com/) local repo from helmfile.

To use this container, mount the helmfile directory to `/opt/helmfile` in the container. After the container finishes, the directory `/charts` contains all downloaded charts.

```bash
$  docker run -v "/mydir/helmfile:/opt/helmfile" quay.io/alexcheng1982/helmfile-charts-local-repo
```

The code is looking for `helmfile.lock` files in the base directory. You need to run `helmfile deps` command first to generate this lock file.