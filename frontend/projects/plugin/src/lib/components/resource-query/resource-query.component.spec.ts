import {ComponentFixture, TestBed} from '@angular/core/testing';
import {of, Subject} from 'rxjs';

import {ResourceQueryComponent} from './resource-query.component';
import {ResourceQueryConfig} from '../../models/resource-query-config';

describe('ResourceQueryComponent', () => {
    let component: ResourceQueryComponent;
    let fixture: ComponentFixture<ResourceQueryComponent>;
    let save$: Subject<void>;

    const emptyConfig = {
        resultPvName: '',
    } satisfies Partial<ResourceQueryConfig>;
    const validConfig = {...emptyConfig, resultPvName: 'x'} satisfies Partial<ResourceQueryConfig>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [ResourceQueryComponent]
        })
            .overrideTemplate(ResourceQueryComponent, '')
            .compileComponents();

        save$ = new Subject<void>();

        fixture = TestBed.createComponent(ResourceQueryComponent);
        component = fixture.componentInstance;

        component.save$ = save$;
        component.disabled$ = of(false);
        component.pluginId = 'openklant';
        component.prefillConfiguration$ = of(emptyConfig as ResourceQueryConfig);

        fixture.detectChanges();
    });

    afterEach(() => save$.complete());

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should report an incomplete configuration as invalid', () => {
        const emitted: boolean[] = [];
        component.valid.subscribe(valid => emitted.push(valid));

        component.formValueChange(emptyConfig);

        expect(emitted).toEqual([false]);
    });

    it('should report a configuration with every required field as valid', () => {
        const emitted: boolean[] = [];
        component.valid.subscribe(valid => emitted.push(valid));

        component.formValueChange(validConfig);

        expect(emitted).toEqual([true]);
    });

    it('should emit the configuration on save when it is valid', () => {
        const configurations: unknown[] = [];
        component.configuration.subscribe(configuration => configurations.push(configuration));

        component.formValueChange(validConfig);
        save$.next();

        expect(configurations).toEqual([validConfig]);
    });

    it('should not emit the configuration on save when it is invalid', () => {
        const configurations: unknown[] = [];
        component.configuration.subscribe(configuration => configurations.push(configuration));

        component.formValueChange(emptyConfig);
        save$.next();

        expect(configurations).toEqual([]);
    });
});
