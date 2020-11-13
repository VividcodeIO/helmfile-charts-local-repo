# Helm Charts Local Repo

[![Docker Repository on Quay](https://quay.io/repository/alexcheng1982/helmfile-charts-local-repo/status "Docker Repository on Quay")](https://quay.io/repository/alexcheng1982/helmfile-charts-local-repo)

Download all charts to support offline installation with helmfile.

## Why?

Sometimes you may need to install Helm charts in an airgap environment. One option is to use `helm pull` command to save chart files. However, if a chart has dependencies, you need to pull all the transitive dependencies. This should be automated.

## How?

This tool is a Docker image that can be run as a tool to download all charts. The starting point is a directory containing the `helmfile.lock` file. 

To use this container, mount the helmfile directory to `/opt/helmfile` in the container. After the container finishes, the directory `/charts` contains all downloaded charts.

```bash
$  docker run -v "/mydir/helmfile:/opt/helmfile" quay.io/alexcheng1982/helmfile-charts-local-repo
```

The code is looking for `helmfile.lock` files in the base directory. You need to run `helmfile deps` command first to generate this lock file.