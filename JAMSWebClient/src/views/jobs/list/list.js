import config from "../../../config";
import * as flashes from "../../../flashes";
import {formatDateTime} from "../../../date";

const flashIdLoadingJobsFailed = 1;
const flashIdRemovingJobFailed = 2;

export default {
	beforeDestroy() {
		clearInterval(this.jobsIntervalId);
		clearInterval(this.serverLoadIntervalId);

		window.removeEventListener("online", this.getJobs);
		window.removeEventListener("online", this.getServerLoad);
	},
	created() {
		this.getJobs(true);
		this.getServerLoad(true);

		this.jobsIntervalId = setInterval(this.getJobs, config.jobsInterval);
		this.serverLoadIntervalId = setInterval(this.getServerLoad, config.serverLoadInterval);

		window.addEventListener("online", this.getJobs);
		window.addEventListener("online", this.getServerLoad);
	},
	data() {
		return {
			jobs: [],
			jobsIntervalId: 0,
			serverLoad: -1,
			serverLoadIntervalId: 0
		};
	},
	methods: {
		getDownloadUrl(workspaceId) {
			return config.apiBaseUrl + "/workspace/download/" + workspaceId;
		},

		formatDateTime,

		getJobs(force = false) {
			if (!this.$store.state.isOnline) {
				return;
			}

			if (!force && !this.$store.state.isConnected) {
				return;
			}

			flashes.clear(flashIdLoadingJobsFailed);

			const url = config.apiBaseUrl + "/job/find";

			this.$http.get(url).then((response) => {
				response.json().then((data) => {
					this.jobs = data.jobs;
				}, (response) => {
					console.error("jobs: Parsing JSON response failed:", response);
				});
			}, (response) => {
				flashes.error("Job list couldn’t be loaded", flashIdLoadingJobsFailed);
			});
		},

		getServerLoad(force = false) {
			if (!this.$store.state.isOnline) {
				return;
			}

			if (!force && !this.$store.state.isConnected) {
				return;
			}

			const url = config.apiBaseUrl + "/job/load";

			this.$http.get(url).then((response) => {
				response.json().then((data) => {
					this.serverLoad = data;
				}, (response) => {
					console.error("jobs: Parsing JSON response failed:", response);
				});
			});
		},

		removeJob(jobId) {
			flashes.clear(flashIdRemovingJobFailed);

			const message = "Remove job?";

			if (!window.confirm(message)) {
				return;
			}

			const url = config.apiBaseUrl + "/job/" + jobId + "/delete";

			this.$http.get(url).then((response) => {
				response.json().then((data) => {
					for (let i = 0; i < this.jobs.length; i++) {
						if (this.jobs[i].id === data.id) {
							this.jobs.splice(i, 1);
							break;
						}
					}
				}, (response) => {
					console.error("jobs: Parsing JSON response failed:", response);
				});
			}, (response) => {
				flashes.error("Job couldn’t be removed", flashIdRemovingJobFailed);
			});
		},

		stopJob(jobId) {
			const message = "Stop job?";

			if (!window.confirm(message)) {
				return;
			}

			const url = config.apiBaseUrl + "/job/" + jobId + "/kill";

			this.$http.get(url).then((response) => {
				response.json().then((data) => {
					for (let i = 0, length = this.jobs.length; i < length; i++) {
						if (this.jobs[i].id === data.job.id) {
							this.jobs[i].PID = -2;
							break;
						}
					}
				}, (response) => {
					console.error("jobs: Parsing JSON response failed:", response);
				});
			}, (response) => {
				flashes.error("Job couldn’t be stopped");
			});
		}
	}
};
