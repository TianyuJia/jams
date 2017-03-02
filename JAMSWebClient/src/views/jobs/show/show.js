import config from "../../../config";
import {formatDateTime, formatDuration} from "../../../date";

export default {
	computed: {
		formattedStartTime: function() {
			return formatDateTime(this.job.startTime);
		},
		formattedDuration: function() {
			return formatDuration(this.duration);
		}
	},
	created() {
		this.getJob();
		this.getLog("error");
		this.getLog("info");
	},
	data() {
		return {
			// Job state
			duration: 0,
			isActive: false,
			job: null,
			progress: 1,
			size: 0,

			// Logs
			errorlog: "",
			infoLog: ""
		};
	},
	methods: {
		getJob() {
			const jobId = this.$route.params.id;
			const url = config.baseUrl + "/job/" + jobId + "/state";

			this.$http.get(url).then((response) => {
				response.json().then((data) => {
					this.duration = data.duration;
					this.isActive = data.active;
					this.job = data.job;
					this.progress = data.progress;
					this.size = data.size;
				}, (response) => {
					console.error("jobs: Parsing JSON response failed:", response);
				});
			}, (response) => {
				this.$store.commit("flashes/add", {
					message: "Job info couldn’t be loaded",
					type: 1
				});
			});
		},
		getLog(type) {
			if (type !== "error" && type !== "info") {
				console.error("jobs show: unknown log type");
				return;
			}

			const jobId = this.$route.params.id;
			const url = config.baseUrl + "/job/" + jobId + "/" + type + "log";

			this.$http.get(url).then((response) => {
				console.debug(response);

				response.blob().then((data) => {
					console.debug(data);

					const reader = new FileReader();
					reader.addEventListener("loadend", () => {
						if (type === "error") {
							this.errorlog = reader.result;
						} else if (type === "info") {
							this.infoLog = reader.result;
						}
					});
					reader.readAsText(data);
				});
			}, (response) => {
				const logType = type === "error" ? "Error" : "Info";
				this.$store.commit("flashes/add", {
					message: logType + " log couldn’t be loaded",
					type: 1
				});
			});
		}
	}
};
