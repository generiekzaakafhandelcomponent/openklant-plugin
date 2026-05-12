import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
} from "@angular/core";
import {
  PluginConfigurationComponent,
  PluginConfigurationData,
  PluginTranslatePipeModule,
} from '@valtimo/plugin';
import {
  BehaviorSubject,
  combineLatest,
  Observable, ReplaySubject,
  Subscription,
  take,
} from "rxjs";
import {Config} from "../../models";
import {FormModule, FormOutput, InputModule} from '@valtimo/components';
import {AsyncPipe, NgIf} from '@angular/common';

@Component({
  selector: 'open-klant-plugin-configuration',
  templateUrl: './open-klant-plugin-configuration.component.html',
  styleUrl: './open-klant-plugin-configuration.component.scss',
  imports: [
    FormModule,
    InputModule,
    PluginTranslatePipeModule,
    AsyncPipe,
    NgIf
  ]
})
export class OpenKlantPluginConfigurationComponent
  implements PluginConfigurationComponent, OnInit, OnDestroy {
  @Input() save$: Observable<void>;
  @Input() disabled$: Observable<boolean>;
  @Input() pluginId: string;
  @Input() prefillConfiguration$: Observable<Config>;

  @Output() valid: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() configuration: EventEmitter<PluginConfigurationData> =
    new EventEmitter<PluginConfigurationData>();

  private saveSubscription: Subscription;

  private readonly config$ = new BehaviorSubject<Config | null>(null);
  private readonly valid$ = new BehaviorSubject<boolean>(false);

  ngOnInit(): void {
    this.openSaveSubscription();
  }

  ngOnDestroy(): void {
    this.saveSubscription?.unsubscribe();
  }

  formValueChange(formOutput: FormOutput): void {
    this.config$.next(formOutput as Config);
    this.handleValid(formOutput as Config);
  }

  private handleValid(formOutput: Config): void {
    // The configuration is valid when a configuration title and url are defined
    const valid = !!(
      formOutput.configurationTitle &&
      formOutput.klantinteractiesUrl &&
      formOutput.token
    );

    this.valid$.next(valid);
    this.valid.emit(valid);
  }

  private openSaveSubscription(): void {
    this.saveSubscription = this.save$?.subscribe((save) => {
      combineLatest([this.config$, this.valid$])
        .pipe(take(1))
        .subscribe(([config, valid]) => {
          if (valid) {
            this.configuration.emit(config);
          }
        });
    });
  }
}
